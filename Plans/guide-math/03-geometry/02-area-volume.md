@@ 1 | MODULE | Phase 2 — Perimeter, Area, Surface Area and Volume (Foundation) | Areas of polygons and circles, composite shapes, nets and cross sections, surface area and volume of solids, density | MUST
# Phase 2: Perimeter, area, surface area and volume

Measurement is the most practical part of geometry: how much paint, flooring, fencing, water, concrete or packaging you need. Every formula here comes from one or two simple ideas (cutting and rearranging, or stacking layers), so focus on **why** each formula works and you won't need to memorise blindly.

**Units:** length in cm or m; area in cm² or m² (squares); volume in cm³ or m³ (cubes). 1 L = 1000 cm³.

@@ 2 | MODULE | Area of plane figures | Rectangles, parallelograms, triangles, trapezoids, composite shapes, circles | MUST
Area counts how many unit squares cover a shape.

@@ 3 | TOPIC | Area of rectangles, parallelograms and triangles | Base × height, why a parallelogram is a rearranged rectangle, half a parallelogram | MUST
# Area of rectangles, parallelograms and triangles

## 1. What it covers
- Area as "number of unit squares".
- Rectangle $A = lw$; parallelogram $A = bh$; triangle $A = \frac{1}{2}bh$.
- Choosing the base and the matching **perpendicular** height.
- Perimeter vs area.

## 2. Where it is used
- **Construction and interiors:** flooring, tiles, paint, carpets, land plots.
- **Computer graphics:** triangle areas are used in rendering (barycentric coordinates for shading and texture mapping).
- **Agriculture:** crop yield per area (e.g. kg per bigha or per acre).
- **Screens:** pixel counts ($1920 \times 1080$ is an area).

## 3. What it means
**Rectangle:** $l$ rows of $w$ unit squares → $lw$.

**Parallelogram:** cut off the triangle on one side and slide it to the other: you get a **rectangle** with the same base and the same height. So $A = bh$, where $h$ is the **perpendicular** height (not the slanted side).

**Triangle:** two copies of any triangle fit together into a parallelogram. So a triangle is **half** of a parallelogram: $A = \frac{1}{2}bh$. Any side can be the base, as long as the height is perpendicular to **that** side.

**Perimeter** is the distance around (fence); **area** is the space inside (grass). Shapes with the same perimeter can have very different areas.

## 4. Formula & worked example
$$
A_{\text{rectangle}} = lw \qquad A_{\text{parallelogram}} = bh \qquad A_{\text{triangle}} = \tfrac{1}{2}bh
$$

**Example 1.** A room 4.5 m × 3.2 m. Floor area? → $14.4$ m². Tiles of 0.4 m × 0.4 m needed? → $\frac{14.4}{0.16} = 90$ tiles.

**Example 2.** A parallelogram with base 12 cm, slanted side 7 cm and height 5 cm.
- $A = 12 \times 5 = 60$ cm² (the 7 cm side is not used)

**Example 3.** A triangle with base 10 m and height 6.4 m: $A = 32$ m².

**Example 4.** An obtuse triangle: base 8, the height falls outside the triangle and is 5. Area $= 20$ (still $\frac{1}{2}bh$).

**Example 5 (reverse).** A triangle has area 45 cm² and base 15 cm. Height? → $\frac{2 \times 45}{15} = 6$ cm.

**Example 6.** A rectangle has perimeter 40 m. Compare $15 \times 5$ and $10 \times 10$: areas 75 m² and 100 m² (the square has the most area for a given perimeter).

## 5. Common mistakes
- Using the slanted side as the height.
- Forgetting the $\frac{1}{2}$ for triangles.
- Mixing units (cm and m) or giving area in cm instead of cm².

## 6. Practice
Q: Area of a parallelogram with base 9 and height 4?
A: $36$.

Q: Triangle with base 14 cm and height 9 cm?
A: $63$ cm².

Q: A rectangle has area 72 and width 6. Length?
A: $12$.

Q: Why is a triangle's area half of base × height?
A: Two copies of the triangle form a parallelogram with area base × height.

Q: How many 30 cm × 30 cm tiles cover a 3 m × 2.7 m floor?
A: $\frac{8.1}{0.09} = 90$.

@@ 3 | TOPIC | Area of trapezoids and composite figures | Trapezoid formula, splitting and subtracting shapes, polygons on a grid | SHOULD
# Area of trapezoids and composite figures

## 1. What it covers
- The trapezoid area $A = \frac{1}{2}(a + b)h$.
- **Composite** shapes: split into simple pieces, or subtract a hole.
- Areas of polygons drawn on a grid.

## 2. Where it is used
- **Architecture:** L-shaped rooms, floor plans, land plots with irregular boundaries.
- **Civil engineering:** cross-sections of canals, embankments and roads are often trapezoids.
- **Surveying:** the area under a curve is approximated with trapezoids (the trapezoidal rule, used in calculus and numerical software).
- **Design:** cutting material with holes.

## 3. What it means
**Trapezoid:** two copies, one flipped, fit together into a parallelogram with base $a + b$ and height $h$. Half of that is $\frac{1}{2}(a + b)h$. Another view: the **average** of the two parallel sides, times the height.

**Composite shapes:**
- **Add:** split into rectangles, triangles and trapezoids; add the areas.
- **Subtract:** take a big simple shape and remove the missing part (a frame = outer rectangle − inner rectangle).

## 4. Formula & worked example
$$
A_{\text{trapezoid}} = \frac{(a + b)}{2}\,h
$$

**Example 1.** Trapezoid with parallel sides 8 m and 14 m, height 5 m: $A = \frac{22}{2} \times 5 = 55$ m².

**Example 2 (canal).** A canal cross-section is a trapezoid: top 6 m, bottom 3 m, depth 2 m. Water flowing 1.5 m/s: volume per second?
- Area $= \frac{9}{2} \times 2 = 9$ m² → $9 \times 1.5 = 13.5$ m³ per second

**Example 3 (L-shape).** A 10 × 8 rectangle with a 4 × 3 corner cut out.
- $80 - 12 = 68$

**Example 4 (frame).** A photo 20 × 30 cm in a frame 26 × 36 cm. Frame area? → $936 - 600 = 336$ cm²

**Example 5 (grid).** A pentagon on a grid: split it into a rectangle 6 × 4 and a triangle on top with base 6, height 3. Area $= 24 + 9 = 33$.

## 5. Common mistakes
- Using a slanted leg as the height of a trapezoid.
- Overlapping pieces when splitting (counting an area twice).
- Forgetting to subtract holes.

## 6. Practice
Q: Trapezoid with bases 5 and 11, height 4?
A: $32$.

Q: A 12 × 9 rectangle with a 3 × 3 square hole. Area?
A: $99$.

Q: A trapezoid has area 60, height 6 and one base 8. Other base?
A: $\frac{120}{6} - 8 = 12$.

Q: What is the trapezoid area formula in words?
A: Average of the parallel sides times the height.

Q: A garden is a 10 m square with a 2 m wide path inside along all edges. Path area?
A: $100 - 36 = 64$ m².

@@ 3 | TOPIC | Circles: circumference and area | π, C = 2πr, A = πr², semicircles and composite shapes with circles | MUST
# Circles: circumference and area

## 1. What it covers
- Radius, diameter and the meaning of $\pi$.
- **Circumference** $C = 2\pi r = \pi d$.
- **Area** $A = \pi r^2$ and why.
- Semicircles, rings (annuli) and composite shapes.

## 2. Where it is used
- **Wheels:** one turn of a wheel moves it a distance equal to its circumference (bike speedometers and odometers use this).
- **Engineering:** pipe cross-sections, flow rates, circular tanks.
- **Pizza economics:** a 12-inch pizza has more than twice the area of an 8-inch one ($\frac{144}{64} = 2.25$).
- **UI and graphics:** circular buttons, progress rings, radar charts.

## 3. What it means
For **every** circle, circumference ÷ diameter is the same number: $\pi \approx 3.14159$. So $C = \pi d = 2\pi r$.

**Why $A = \pi r^2$:** cut a circle into many thin pizza slices and lay them alternately point-up and point-down. They form an almost-rectangle with height $r$ and width half the circumference, $\pi r$. Area $\approx \pi r \times r = \pi r^2$. With more slices it becomes exact.

**Ring (annulus):** big circle minus small circle: $\pi(R^2 - r^2)$.

## 4. Formula & worked example
$$
C = 2\pi r = \pi d \qquad A = \pi r^2 \qquad A_{\text{ring}} = \pi(R^2 - r^2)
$$

**Example 1.** Radius 7 cm: $C = 14\pi \approx 43.98$ cm; $A = 49\pi \approx 153.94$ cm².

**Example 2 (wheel).** A bike wheel of diameter 70 cm. How many turns to go 1 km?
- $C = 70\pi \approx 219.9$ cm → $\frac{100\,000}{219.9} \approx 455$ turns

**Example 3 (reverse).** A circle has area $81\pi$. Radius? → $9$. Circumference? → $18\pi$.

**Example 4 (semicircle).** A window is a 1.2 m × 1.5 m rectangle with a semicircle (diameter 1.2 m) on top. Area?
- $1.8 + \frac{1}{2}\pi(0.6)^2 \approx 1.8 + 0.565 = 2.365$ m²

**Example 5 (ring).** A circular path 2 m wide around a pond of radius 10 m.
- $\pi(12^2 - 10^2) = 44\pi \approx 138.2$ m²

**Example 6 (pizza).** 12-inch vs two 8-inch pizzas.
- $\pi(6)^2 = 36\pi$ vs $2\pi(4)^2 = 32\pi$ → the single 12-inch has more.

## 5. Common mistakes
- Using the diameter in $\pi r^2$.
- Confusing circumference (length) with area.
- Squaring $\pi r$ instead of $r$: $\pi r^2 \ne (\pi r)^2$.

## 6. Practice
Q: Circumference of a circle with diameter 10?
A: $10\pi \approx 31.42$.

Q: Area of a circle with radius 5?
A: $25\pi \approx 78.54$.

Q: Circumference $= 30\pi$. Area?
A: $r = 15$ → $225\pi$.

Q: Area of a semicircle of radius 4?
A: $8\pi$.

Q: Doubling the radius multiplies the area by?
A: $4$.

@@ 2 | MODULE | Solids: surface area and volume | Nets, cross sections, surface area, volume of prisms, cylinders, pyramids, cones, spheres, density | SHOULD
Surface area is wrapping paper; volume is how much it holds.

@@ 3 | TOPIC | 3D figures: nets, cross sections and solids of revolution | Faces, edges, vertices, Euler's formula, nets, slicing solids, rotating shapes | SHOULD
# 3D figures: nets, cross sections and solids of revolution

## 1. What it covers
- Prisms, pyramids, cylinders, cones and spheres: faces, edges, vertices.
- **Euler's formula** $V - E + F = 2$.
- **Nets**: flat patterns that fold into solids.
- **Cross sections**: the 2D shape you get by slicing a solid.
- **Solids of revolution**: spinning a 2D shape around an axis.

## 2. Where it is used
- **Packaging design:** boxes are cut from nets.
- **Medical imaging:** CT and MRI scans are stacks of cross sections.
- **3D printing:** the slicer software cuts a model into horizontal cross sections, printed layer by layer.
- **Manufacturing:** lathes and pottery wheels make solids of revolution (vases, table legs, bottles).
- **3D graphics:** meshes have vertices, edges and faces.

## 3. What it means
- **Prism:** two identical, parallel bases joined by rectangles (a box, a Toblerone bar).
- **Pyramid:** one base, triangular sides meeting at a point.
- **Cylinder / cone:** like a prism / pyramid with a circular base.
- **Sphere:** all points at the same distance from a centre.

**Euler's formula:** for any convex polyhedron, vertices − edges + faces $= 2$. A cube: $8 - 12 + 6 = 2$.

**Cross sections:** slicing a cube parallel to a face gives a square; slicing diagonally can give a rectangle, triangle or even a hexagon. Slicing a cone parallel to the base gives a circle; at an angle, an ellipse (Phase 6).

**Solids of revolution:** spin a rectangle around one side → cylinder; a right triangle around a leg → cone; a semicircle around its diameter → sphere.

## 4. Formula & worked example
$$
V - E + F = 2
$$

**Example 1.** A triangular prism: $V = 6$, $E = 9$, $F = 5$ → $6 - 9 + 5 = 2$ ✓

**Example 2.** A polyhedron has 12 vertices and 30 edges. Faces? → $F = 2 - 12 + 30 = 20$ (an icosahedron).

**Example 3.** Which net folds into a cube? A cross shape of 6 squares (one row of 4 with one square above and one below) does; a straight row of 6 does not.

**Example 4.** Cross section of a cylinder sliced vertically through its axis? → a rectangle (height × diameter).

**Example 5.** Spinning a rectangle 3 wide and 5 tall around its 5 cm side gives a cylinder of radius 3 and height 5.

## 5. Common mistakes
- Counting edges twice when listing them face by face (each edge belongs to two faces).
- Assuming a cross section is always the same as the base.
- Mixing up which side is the axis in solids of revolution (that side becomes the height).

## 6. Practice
Q: Faces, edges and vertices of a square pyramid?
A: $F = 5$, $E = 8$, $V = 5$.

Q: A polyhedron has 8 faces and 6 vertices. Edges?
A: $E = V + F - 2 = 12$ (an octahedron).

Q: Cross section of a sphere?
A: Always a circle.

Q: What solid comes from spinning a right triangle around one leg?
A: A cone.

Q: Why do 3D printers use cross sections?
A: They print the object one horizontal slice (layer) at a time.

@@ 3 | TOPIC | Surface area of prisms, cylinders, pyramids, cones and spheres | Adding face areas from nets, lateral area, slant height, sphere surface area | SHOULD
# Surface area of prisms, cylinders, pyramids, cones and spheres

## 1. What it covers
- Surface area as the total area of all faces (the area of the net).
- Prisms and cylinders; pyramids and cones (with **slant height**); spheres.
- Lateral (side) area vs total surface area.

## 2. Where it is used
- **Packaging:** cardboard needed for a box; label on a can (cylinder lateral area).
- **Painting and coating:** paint for a tank, plating a part.
- **Heat transfer:** objects lose heat through their surface; small animals lose heat faster because they have more surface per unit volume.
- **Chemistry:** finely ground powders react faster (more surface area).

## 3. What it means
Unfold the solid into its net and add up the areas.
- **Prism:** 2 bases + rectangles around the side. The side rectangles together form one big rectangle: perimeter of base × height.
- **Cylinder:** 2 circles + a rectangle (unrolled label) whose width is the circumference $2\pi r$.
- **Pyramid:** base + triangles with height = **slant height** $\ell$ (measured along the face, not straight down).
- **Cone:** base circle + curved surface $\pi r\ell$.
- **Sphere:** $4\pi r^2$, exactly 4 times the area of its largest circle (Archimedes).

## 4. Formula & worked example
$$
SA_{\text{prism}} = 2B + Ph \qquad SA_{\text{cylinder}} = 2\pi r^2 + 2\pi rh \qquad SA_{\text{cone}} = \pi r^2 + \pi r\ell \qquad SA_{\text{sphere}} = 4\pi r^2
$$
$$
SA_{\text{pyramid}} = B + \tfrac{1}{2}P\ell \qquad \ell = \sqrt{r^2 + h^2}\ (\text{cone})
$$

**Example 1.** A box 30 × 20 × 10 cm: $2(600 + 300 + 200) = 2200$ cm²

**Example 2.** A can with radius 4 cm, height 11 cm.
- Label: $2\pi(4)(11) = 88\pi \approx 276.5$ cm²; total: $88\pi + 32\pi = 120\pi \approx 377.0$ cm²

**Example 3.** A square pyramid, base 10 m, slant height 13 m.
- $100 + 4 \times \frac{1}{2}(10)(13) = 100 + 260 = 360$ m²

**Example 4.** A cone with radius 6 and height 8: $\ell = 10$ → $36\pi + 60\pi = 96\pi \approx 301.6$

**Example 5.** A ball of radius 11 cm: $4\pi(121) = 484\pi \approx 1520.5$ cm²

## 5. Common mistakes
- Using the vertical height instead of the slant height for cones and pyramids.
- Forgetting the bases (or including a base that's open, like an open-top box or a cup).
- Using diameter instead of radius.

## 6. Practice
Q: Surface area of a cube with edge 5?
A: $150$.

Q: Lateral area of a cylinder with $r = 3$, $h = 10$?
A: $60\pi$.

Q: Surface area of a sphere of radius 3?
A: $36\pi$.

Q: Slant height of a cone with $r = 5$, $h = 12$?
A: $13$.

Q: Total surface area of that cone?
A: $25\pi + 65\pi = 90\pi$.

@@ 3 | TOPIC | Volume of prisms and cylinders | Base area × height, Cavalieri's principle, unit conversions for volume | SHOULD
# Volume of prisms and cylinders

## 1. What it covers
- Volume as "number of unit cubes".
- $V = Bh$ for any prism or cylinder.
- **Cavalieri's principle**: slanted (oblique) solids have the same volume.
- Converting volume units (cm³, m³, litres).

## 2. Where it is used
- **Water and storage:** tanks, swimming pools, rainwater harvesting.
- **Construction:** concrete for slabs, columns and foundations.
- **Shipping:** container volume and cargo planning.
- **Cloud and data centres:** cooling air volume per rack.
- **Cooking:** pan capacity.

## 3. What it means
A prism or cylinder is a stack of identical layers. Each layer has the base area $B$, and there are $h$ layers of thickness 1. So $V = Bh$.

**Cavalieri's principle:** a stack of coins has the same volume whether it's straight or pushed into a slant, because every layer is the same. So an **oblique** prism or cylinder also has $V = Bh$, with $h$ the **perpendicular** height.

**Units:** $1 \text{ m}^3 = 1000$ L; $1 \text{ L} = 1000 \text{ cm}^3$; $1 \text{ mL} = 1 \text{ cm}^3$.

## 4. Formula & worked example
$$
V_{\text{prism}} = Bh \qquad V_{\text{box}} = lwh \qquad V_{\text{cylinder}} = \pi r^2 h
$$

**Example 1.** A fish tank 60 × 30 × 40 cm: $72\,000$ cm³ $= 72$ L.

**Example 2.** A cylindrical tank of radius 1.2 m and height 2 m.
- $\pi(1.44)(2) \approx 9.05$ m³ $\approx 9048$ L

**Example 3.** A triangular prism: triangle base 6 cm, triangle height 4 cm, prism length 15 cm.
- $B = 12$ → $V = 180$ cm³

**Example 4 (concrete).** A slab 5 m × 4 m × 0.15 m: $3$ m³ of concrete.

**Example 5 (reverse).** A can must hold 500 mL with radius 4 cm. Height?
- $500 = 16\pi h$ → $h \approx 9.95$ cm

## 5. Common mistakes
- Using the slanted edge of an oblique prism instead of the perpendicular height.
- Mixing cm and m in one calculation.
- Converting m³ to cm³ with ×100 instead of ×1 000 000.

## 6. Practice
Q: Volume of a cube with edge 4 cm?
A: $64$ cm³.

Q: Volume of a cylinder with $r = 5$, $h = 8$?
A: $200\pi \approx 628.3$.

Q: A pool 25 m × 10 m × 1.8 m holds how many litres?
A: $450$ m³ $= 450\,000$ L.

Q: State Cavalieri's principle.
A: Solids with equal heights and equal cross-sectional areas at every level have equal volumes.

Q: How many litres in 1 m³?
A: $1000$.

@@ 3 | TOPIC | Volume of pyramids, cones and spheres | The one-third rule, sphere volume, composite solids | SHOULD
# Volume of pyramids, cones and spheres

## 1. What it covers
- Pyramids and cones: $V = \frac{1}{3}Bh$.
- Spheres: $V = \frac{4}{3}\pi r^3$; hemispheres.
- Composite solids (e.g. a cylinder with a cone or hemisphere on top).

## 2. Where it is used
- **Food and packaging:** ice-cream cones, conical paper cups.
- **Construction:** sand and gravel piles form cones; grain silos are cylinders with conical roofs.
- **Sports:** ball volumes and air pressure.
- **Science:** cell, droplet and planet volumes (all near-spheres).
- **Medicine:** tumour volume estimates.

## 3. What it means
**One-third rule:** a pyramid holds exactly **one third** of the prism with the same base and height. You can check this with water: three full pyramids exactly fill the prism. The same holds for a cone in a cylinder.

**Sphere:** a sphere fits inside a cylinder of the same radius and height $2r$; Archimedes showed it takes up exactly $\frac{2}{3}$ of that cylinder: $\frac{2}{3}\pi r^2(2r) = \frac{4}{3}\pi r^3$. (He was so proud of this that he asked for it to be carved on his tomb.)

Because volume grows with $r^3$, doubling the radius makes a sphere **8 times** bigger.

## 4. Formula & worked example
$$
V_{\text{pyramid}} = \tfrac{1}{3}Bh \qquad V_{\text{cone}} = \tfrac{1}{3}\pi r^2 h \qquad V_{\text{sphere}} = \tfrac{4}{3}\pi r^3
$$

**Example 1.** A square pyramid, base 9 m, height 10 m: $\frac{1}{3}(81)(10) = 270$ m³

**Example 2.** A cone of radius 3 cm, height 12 cm: $\frac{1}{3}\pi(9)(12) = 36\pi \approx 113.1$ cm³

**Example 3.** A football of radius 11 cm: $\frac{4}{3}\pi(1331) \approx 5575$ cm³ $\approx 5.6$ L

**Example 4 (composite).** An ice-cream cone: cone radius 3 cm, height 10 cm, with a hemisphere of ice cream on top.
- Cone $30\pi$, hemisphere $\frac{2}{3}\pi(27) = 18\pi$ → $48\pi \approx 150.8$ cm³

**Example 5 (sand pile).** A conical pile 2 m high with base radius 3 m. Truckloads of 6 m³?
- $V = \frac{1}{3}\pi(9)(2) = 6\pi \approx 18.85$ m³ → 4 truckloads (3.14 rounded up)

## 5. Common mistakes
- Forgetting the $\frac{1}{3}$ for cones and pyramids.
- Using $r^2$ instead of $r^3$ for spheres.
- Using slant height instead of perpendicular height in the volume formula.

## 6. Practice
Q: Volume of a cone with $r = 6$, $h = 5$?
A: $60\pi$.

Q: Volume of a sphere with radius 3?
A: $36\pi$.

Q: Volume of a hemisphere with radius 6?
A: $144\pi$.

Q: A pyramid and a prism share a base and height. Ratio of volumes?
A: $1 : 3$.

Q: Doubling a sphere's radius multiplies its volume by?
A: $8$.

@@ 3 | TOPIC | Density and modeling with solids | Density = mass/volume, population density, modelling real objects with solids | SHOULD
# Density and modeling with solids

## 1. What it covers
- **Density** $\rho = \frac{m}{V}$ and solving for mass or volume.
- Other densities: population per km², pixels per inch, energy density.
- Modelling real objects as combinations of basic solids.
- Optimisation questions (most volume for least material).

## 2. Where it is used
- **Engineering:** the weight of a steel beam or concrete slab from its volume.
- **Shipping and logistics:** volumetric weight pricing.
- **Urban planning:** population density (Dhaka is one of the densest cities in the world).
- **Screens:** pixel density (PPI).
- **Batteries:** energy density (Wh/kg) for phones and electric vehicles.

## 3. What it means
**Density** tells you how much mass is packed into each unit of volume. Iron is denser than wood, so an iron ball is heavier than a wooden ball of the same size. Water's density is 1 g/cm³ (1000 kg/m³); anything denser sinks.

**Modelling:** real objects are rarely perfect shapes, but you can approximate them: a tree trunk as a cylinder, a hill as a cone, a grain as a sphere. State your assumptions and check that the answer is reasonable.

**Optimisation:** for a given volume, a sphere has the least surface area; among cylinders, the one with height = diameter uses the least material (cans are often close to this).

## 4. Formula & worked example
$$
\rho = \frac{m}{V} \qquad m = \rho V \qquad V = \frac{m}{\rho}
$$

**Example 1.** A steel cube with edge 10 cm, steel density 7.85 g/cm³. Mass?
- $V = 1000$ cm³ → $m = 7850$ g $= 7.85$ kg

**Example 2.** A stone of mass 540 g displaces 200 cm³ of water. Density? → $2.7$ g/cm³ (likely granite or aluminium-like). Does it float? No ($> 1$).

**Example 3 (tree).** A log is modelled as a cylinder of radius 0.25 m and length 6 m, wood density 600 kg/m³.
- $V = \pi(0.0625)(6) \approx 1.178$ m³ → $m \approx 707$ kg

**Example 4 (population).** A district has 1.2 million people over 300 km². Density? → $4000$ people/km².

**Example 5 (pixel density).** A 6-inch diagonal phone screen with 2400 × 1080 pixels.
- Diagonal pixels $= \sqrt{2400^2 + 1080^2} \approx 2632$ → $\frac{2632}{6} \approx 439$ PPI

## 5. Common mistakes
- Mixing units (g/cm³ with kg/m³; 1 g/cm³ = 1000 kg/m³).
- Using area instead of volume for mass.
- Forgetting to state modelling assumptions.

## 6. Practice
Q: Mass of 2 L of water?
A: $2$ kg.

Q: Gold is 19.3 g/cm³. Mass of a 5 cm³ gold bar?
A: $96.5$ g.

Q: A 400 g object has volume 500 cm³. Does it float in water?
A: Yes, density $0.8 < 1$.

Q: Population density of 50 000 people in 12.5 km²?
A: $4000$ per km².

Q: Which shape has the smallest surface area for a given volume?
A: A sphere.

@@ 3 | TOPIC | Practice & review: Area and volume | Mixed problem set with worked answers | SHOULD
# Practice & review: Area and volume

**How to use this:** 45 minutes with a calculator. Leave answers in terms of $\pi$ where possible.

## Problems
1. A parallelogram has base 15 cm and area 105 cm². Height?
2. A trapezoid-shaped plot has parallel sides 40 m and 25 m, 18 m apart. Area?
3. A running track is a rectangle 100 m × 60 m with semicircles (diameter 60 m) on both short ends. Perimeter and area enclosed?
4. A polyhedron has 9 faces and 9 vertices. How many edges?
5. Surface area of a closed cylinder with diameter 10 cm and height 12 cm.
6. A cone has radius 9 cm and slant height 15 cm. Height and volume?
7. A water tank is a cylinder (radius 1 m, height 3 m) with a hemisphere on top. Volume in litres?
8. A metal sphere of radius 3 cm is melted and recast into a cylinder of radius 2 cm. Height of the cylinder?
9. Aluminium is 2.7 g/cm³. Mass of a sheet 1 m × 0.5 m × 2 mm?

## Answers
1. $7$ cm.
2. $\frac{65}{2} \times 18 = 585$ m².
3. Perimeter $= 200 + 60\pi \approx 388.5$ m; area $= 6000 + 900\pi \approx 8827$ m².
4. $E = 9 + 9 - 2 = 16$.
5. $2\pi(25) + 2\pi(5)(12) = 50\pi + 120\pi = 170\pi \approx 534.1$ cm².
6. $h = \sqrt{225 - 81} = 12$; $V = \frac{1}{3}\pi(81)(12) = 324\pi \approx 1017.9$ cm³.
7. $3\pi + \frac{2}{3}\pi = \frac{11\pi}{3} \approx 11.52$ m³ $\approx 11\,519$ L.
8. $\frac{4}{3}\pi(27) = 36\pi = 4\pi h$ → $h = 9$ cm.
9. $V = 100 \times 50 \times 0.2 = 1000$ cm³ → $2700$ g $= 2.7$ kg.

## Flashcards
Q: Area of a trapezoid?
A: $\frac{(a + b)}{2}h$.

Q: Volume of a cone?
A: $\frac{1}{3}\pi r^2h$.

Q: Surface area of a sphere?
A: $4\pi r^2$.

Q: Euler's formula for polyhedra?
A: $V - E + F = 2$.
