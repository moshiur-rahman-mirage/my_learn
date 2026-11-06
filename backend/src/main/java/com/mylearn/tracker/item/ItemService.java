package com.mylearn.tracker.item;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.exception.StaleUpdateException;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.item.HierarchyQueries.PathEntry;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemDetailResponse;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.MoveItemRequest;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.item.dto.UpdateItemRequest;
import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.progress.Progress;
import com.mylearn.tracker.progress.ProgressQueries;
import com.mylearn.tracker.review.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mylearn.tracker.common.Text.blankToNull;

/**
 * All operations on learning items and the hierarchy rules behind them.
 *
 * <p>Rules (see docs/ARCHITECTURE.md §4):
 * <ul>
 *   <li>Only a TOPIC without children (a "checkable leaf") can be completed.</li>
 *   <li>An item that gains a child loses its own completion, in the same transaction.</li>
 *   <li>A parent must be in the same plan; no item may become its own ancestor.</li>
 *   <li>Siblings always have a gapless display order 0..n-1.</li>
 *   <li>Every write touches the plan's last-activity time.</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final LearningItemRepository items;
    private final PlanService planService;
    private final HierarchyQueries hierarchy;
    private final ProgressQueries progress;
    private final ItemExtrasQueries extras;
    private final ReviewService reviews;

    public ItemService(LearningItemRepository items, PlanService planService,
                       HierarchyQueries hierarchy, ProgressQueries progress, ItemExtrasQueries extras,
                       ReviewService reviews) {
        this.items = items;
        this.extras = extras;
        this.reviews = reviews;
        this.planService = planService;
        this.hierarchy = hierarchy;
        this.progress = progress;
    }

    // --- create -----------------------------------------------------------------------------

    @Transactional
    public ItemResponse create(Long planId, CreateItemRequest request) {
        LearningPlan plan = planService.findPlan(planId);
        LearningItem parent = request.parentId() == null ? null : resolveParent(request.parentId(), planId);

        List<LearningItem> siblings = siblings(planId, request.parentId());
        int position = resolvePosition(request.position(), siblings.size());

        LearningItem item = new LearningItem(plan, parent, request.type(), request.name().strip(),
                blankToNull(request.description()), request.priority());
        siblings.add(position, item);
        renumber(siblings);
        items.save(item);
        plan.touch();
        return ItemResponse.from(item);
    }

    // --- read -------------------------------------------------------------------------------

    /** The whole roadmap of a plan as a tree with rolled-up progress (one query). */
    public List<TreeNode> tree(Long planId) {
        planService.findPlan(planId); // 404 for an unknown plan rather than an empty tree
        return TreeBuilder.build(items.findAllByPlan(planId), extras.forPlan(planId));
    }

    public ItemDetailResponse detail(Long id) {
        LearningItem item = findItem(id, "Item");
        int childCount = items.findChildren(id).size();
        boolean leaf = childCount == 0;
        List<PathEntry> path = hierarchy.pathTo(id);
        return new ItemDetailResponse(ItemResponse.from(item), leaf, leaf && item.getType() == ItemType.TOPIC,
                childCount, progress.progressOfItem(id), path.subList(0, path.size() - 1));
    }

    public Progress progressOf(Long id) {
        findItem(id, "Item");
        return progress.progressOfItem(id);
    }

    // --- update content ---------------------------------------------------------------------

    @Transactional
    public ItemResponse update(Long id, UpdateItemRequest request) {
        LearningItem item = findItem(id, "Item");
        if (item.getVersion() != request.version()) {
            throw new StaleUpdateException("Item", id, request.version(), item.getVersion());
        }
        // Modules are never checkable, so turning a ticked topic into a module drops the tick.
        if (request.type() == ItemType.MODULE && item.isCompleted()) {
            item.clearCompletion();
            reviews.cancel(id);
        }
        item.setType(request.type());
        item.setName(request.name().strip());
        item.setDescription(blankToNull(request.description()));
        item.setPriority(request.priority());
        items.saveAndFlush(item); // flush now so the response carries the incremented version
        planService.touch(item.getPlanId());
        return ItemResponse.from(item);
    }

    // --- completion -------------------------------------------------------------------------

    @Transactional
    public ItemResponse setCompletion(Long id, boolean completed) {
        LearningItem item = findItem(id, "Item");
        if (item.getType() != ItemType.TOPIC || items.existsByParent_Id(id)) {
            throw new ConflictException("Only topics without sub-items can be completed; "
                    + "progress of '%s' is calculated from its contents".formatted(item.getName()));
        }
        if (completed && !item.isCompleted()) {
            item.markCompleted(Timestamps.now());
            reviews.schedule(id);
        } else if (!completed) {
            item.clearCompletion();
            reviews.cancel(id);
        }
        planService.touch(item.getPlanId());
        return ItemResponse.from(item);
    }

    // --- structure: move & reorder ----------------------------------------------------------

    /** Moves an item (with its subtree) under another parent of the same plan, or to the root. */
    @Transactional
    public ItemResponse move(Long id, MoveItemRequest request) {
        LearningItem item = findItem(id, "Item");
        Long planId = item.getPlanId();
        Long oldParentId = item.getParentId();
        Long newParentId = request.parentId();

        LearningItem newParent = null;
        if (newParentId != null) {
            if (newParentId.equals(id)) {
                throw new ConflictException("An item cannot be its own parent");
            }
            newParent = resolveParent(newParentId, planId);
            if (hierarchy.isSelfOrAncestor(id, newParentId)) {
                throw new ConflictException("Cannot move '%s' into its own sub-item '%s'"
                        .formatted(item.getName(), newParent.getName()));
            }
        }

        List<LearningItem> oldSiblings = siblings(planId, oldParentId);
        oldSiblings.removeIf(s -> s.getId().equals(id));
        boolean sameParent = Objects.equals(oldParentId, newParentId);
        List<LearningItem> newSiblings = sameParent ? oldSiblings : siblings(planId, newParentId);
        int position = resolvePosition(request.position(), newSiblings.size());

        item.setParent(newParent);
        newSiblings.add(position, item);
        if (!sameParent) {
            renumber(oldSiblings);
        }
        renumber(newSiblings);
        planService.touch(planId);
        return ItemResponse.from(item);
    }

    /** Changes an item's position among its current siblings. */
    @Transactional
    public ItemResponse reorder(Long id, int position) {
        LearningItem item = findItem(id, "Item");
        List<LearningItem> siblings = siblings(item.getPlanId(), item.getParentId());
        siblings.removeIf(s -> s.getId().equals(id));
        if (position > siblings.size()) {
            throw new BusinessRuleException("Position %d is out of range (0..%d)".formatted(position, siblings.size()));
        }
        siblings.add(position, item);
        renumber(siblings);
        planService.touch(item.getPlanId());
        return ItemResponse.from(item);
    }

    // --- delete -----------------------------------------------------------------------------

    /**
     * Deletes the item and its whole subtree in one statement (DB cascade), then closes the gap in
     * the former siblings' display order. Runs in a single transaction.
     */
    @Transactional
    public void delete(Long id) {
        LearningItem item = findItem(id, "Item");
        Long planId = item.getPlanId();
        Long parentId = item.getParentId();

        items.deleteWithSubtree(id);
        renumber(siblings(planId, parentId));
        planService.touch(planId);
    }

    // --- helpers ----------------------------------------------------------------------------

    LearningItem findItem(Long id, String label) {
        return items.findById(id).orElseThrow(() -> new NotFoundException(label, id));
    }

    /**
     * Loads a prospective parent, checks it is in the given plan, and clears its completion. A
     * leaf that gains a child is no longer a leaf, so its own tick becomes meaningless.
     */
    private LearningItem resolveParent(Long parentId, Long planId) {
        LearningItem parent = findItem(parentId, "Parent item");
        if (!parent.getPlanId().equals(planId)) {
            throw new BusinessRuleException("Parent item %d belongs to a different plan".formatted(parentId));
        }
        if (parent.isCompleted()) {
            parent.clearCompletion();
            reviews.cancel(parentId);
        }
        return parent;
    }

    private static int resolvePosition(Integer requested, int siblingCount) {
        int position = requested == null ? siblingCount : requested;
        if (position > siblingCount) {
            throw new BusinessRuleException("Position %d is out of range (0..%d)".formatted(position, siblingCount));
        }
        return position;
    }

    /** Mutable, ordered list of the children of {@code parentId} (or the plan's roots). */
    private List<LearningItem> siblings(Long planId, Long parentId) {
        return new ArrayList<>(parentId == null ? items.findRoots(planId) : items.findChildren(parentId));
    }

    /** Keeps sibling order gapless (0..n-1). Only rows whose position changed are updated. */
    private static void renumber(List<LearningItem> siblings) {
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i).getDisplayOrder() != i) {
                siblings.get(i).setDisplayOrder(i);
            }
        }
    }
}
