package com.mylearn.tracker.item;

/**
 * MODULE = container (never checkable). TOPIC = unit of learning; a topic without children is a
 * checkable leaf. Both may have children, at any depth.
 */
public enum ItemType {
    MODULE,
    TOPIC
}
