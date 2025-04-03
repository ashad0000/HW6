/******************************************************************
 *
 *   Abdul / 272
 *
 *   Note, additional comments provided throughout this source code
 *   is for educational purposes
 *
 ********************************************************************/

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class PriorityQueue<E,P>
 *
 * The class implements a priority queue utilizing a min heap. The underlying
 * data structure that implements the heap is an array of type 'Node'. Each
 * element's value and priority data types are defined using a class generic
 * interface.
 *
 * On instantiation of this class, the data types of 'E' and 'P' are specified.
 * In this case, the type 'E' is the element's value data type, and 'P' is the
 * priority value data type. The priority object specified needs to have a
 * comparable operator implemented (if the default comparable operator will not
 * suffice); case in point, if using data type Integer, it has a pre-defined
 * comparable operator. But, this class  allows for more sophisticated
 * priority comparisons (e.g., see below on aging discussions as we also
 * discussed in class).
 *
 * To allow the application (aka, user of this class) to have very basic priority
 * management, the class does provide a method, changePriority(), such that you
 * can change a priority of an element already on the queue. The invoker will need
 * to know the node's element to use this (aka, have a handle to it), which is
 * returned from the method which adds the element to the queue. This is for very
 * basic priority management. Additionally, the invoker can dequeue an element
 * no matter where it is on the queue before it gets processes (popped off), and
 * later, if desired, enqueue it with a higher and/or lower priority.
 *
 * A more sophisticated approach would be to implement a weighted priority scheme
 * that optionally allows aging and/or fair scheduling techniques to ensure there
 * is no starvation. This is simple to do with this class. It is accomplished, by
 * specifying an object type for the priority that supports this capability, and
 * has defined  an appropriate comparable operator.
 *
 * Public Methods:
 * ---------------
 *
 * int size()           - Returns the number of elements in the queue
 * boolean isEmpty()    - Returns true if this collection contains no elements.
 * void clear()         - Removes all of the elements from this priority queue.
 * Node peek()          - Retrieves, but does not remove, the head of this queue, or
 *                        returns null if this queue is empty.
 * Node add(E,P)        - Inserts the specified element into this priority queue.
 * Node offer(E,P)      - Inserts the specified element into this priority queue.
 * Node remove()        - Retrieves and removes the head of this queue. This
 *                        method differs from poll only in that it throws an
 *                        exception if this queue is empty.
 * Node poll()          - Retrieves and removes the head of this queue, or returns
 *                        null if this queue is empty.
 * boolean contains(E)  - Returns true if element 'E' is in queue, else false.
 * ArrayList<E> toArray - returns an array consisting of the element values in the
 *                        queue.
 * void printPriorityQueue - This method is for debugging purposes, it will print
 *                        out the min heap
 *
 *
 * Additional methods: The type 'Node' is returned from several methods. It
 * acts as a handle to the queued object when it is on the priority queue. The
 * following methods are available on the object 'Node' when the object is still
 * on the queue:
 *  - changePriority    - given a handle to a node, you can change its priority
 *  - remove            - given a handle to a node, you can remove it from the
 *                        queue directly
 *
 */

class PriorityQueue<E, P> {

    private static final int DEFAULT_CAPACITY = 10;  // initial queue size

    final Comparator<P> comparator;
    final ArrayList<Node> tree;  // The Heap is stored in an array as a tree
    // with the root at index 0 (not 1)

    /*
     * Constructors
     */

    public PriorityQueue() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public PriorityQueue(int capacity) {
        this(capacity, (a, b) -> ((Comparable<P>) a).compareTo(b));
    }

    public PriorityQueue(int capacity, Comparator<P> comparator) {
        tree = new ArrayList<>(capacity);
        this.comparator = comparator;
    }

    /*
     * Miscellaneous Methods
     */

    public int size()               { return tree.size(); }
    public boolean isEmpty()        { return tree.size() == 0; }
    public void clear()             { tree.clear(); }
    public Node offer(E e, P p)     { return add(e, p); }

    /**
     * Public Method peek()
     *
     * Retrieves, but does not remove, the head of this queue, or returns null
     * if this queue is empty.
     *
     * @return: Node    - An element of type Node is returned, else null
     *                    if queue is empty.
     */
    public Node peek() {
        if (size() == 0) {
            return null;
        }
        return tree.get(0);
    }

    /**
     * Public Method add(E,P)
     *
     * Inserts the specified element into min heap as the right most leaf on the
     * lowest level of the tree. It then will pull up the inserted element towards
     * the root until it is in its correct location on the heap.
     *
     * The parameters are the object representing the element to queue, along with
     * its priority object. The method returns the type Node, which allows a handle
     * to the inserted item by the invoking application (if desired). The invoking
     * application has the availability to invoke any method available on type Node
     * after it is returned, including changing the priority of an element while
     * still on the queue.
     *
     * @param: E e          - Element to add to queue
     * @param: P priority   - The priority for the newly added element
     * @return: Node        - Returns an object of type 'Node' representing the
     *                        newly inserted element
     */
    public Node add(E e, P priority) {
        // create node and add it to heap array
        Node newNode = new Node(e, priority, tree.size());  // create node
        tree.add(newNode);  // add to heap array
        pullUp(newNode.idx);  // restore heap-order
        return newNode;  // return handle to node
    }

    /**
     * Public Method contains(E)
     *
     * Returns true if this queue contains the specified element. More formally,
     * returns true if and only if this queue contains at least one element of
     * type 'e' such that o.equals(e).
     *
     * @return: boolean - true if element in queue, else false.
     */
    public boolean contains(E e) {
        // loop through heap to check for value
        for (Node node : tree) {  // iterate over nodes
            if (!node.removed && node.value.equals(e)) {  // check valid and match
                return true;
            }
        }
        return false;
    }

    public Node remove() {
        if (tree.size() == 0) {
            throw new IllegalStateException("PriorityQueue is empty");
        }
        return poll();
    }

    public Node poll() {
        if (tree.size() == 0)
            return null;

        if (tree.size() == 1) {
            final Node removedNode = tree.remove(0);
            removedNode.markRemoved();
            return removedNode;
        } else {
            Node head = tree.get(0);
            head.markRemoved();
            final Node nodeToMoveToHead = tree.remove(tree.size() - 1);
            nodeToMoveToHead.idx = 0;
            tree.set(0, nodeToMoveToHead);
            pushDown(0);
            return head;
        }
    }

    private void pushDown(int i) {
        while (leftChild(i) < size() && compare(tree.get(leftChild(i)).priority, tree.get(i).priority) < 0 ||
                rightChild(i) < size() && compare(tree.get(rightChild(i)).priority, tree.get(i).priority) < 0) {
            int leftChildIdx = leftChild(i);
            int rightChildIdx = rightChild(i);
            if (rightChildIdx >= size() || compare(tree.get(leftChildIdx).priority, tree.get(rightChildIdx).priority) < 0) {
                swap(i, leftChildIdx);
                i = leftChildIdx;
            } else {
                swap(i, rightChildIdx);
                i = rightChildIdx;
            }
        }
    }

    private void pullUp(int i) {
        while (i != 0 && compare(tree.get(parent(i)).priority, tree.get(i).priority) > 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    int leftChild(int i)            { return 2 * i + 1; }
    int rightChild(int i)           { return 2 * i + 2; }
    int parent(int i)               { return (i - 1) / 2; }

    private int compare(P a, P b)   { return comparator.compare(a, b); }

    void swap(int idx1, int idx2) {
        Node node1 = tree.get(idx1);
        Node node2 = tree.get(idx2);

        node1.idx = idx2;
        node2.idx = idx1;

        tree.set(idx1, node2);
        tree.set(idx2, node1);
    }

    public ArrayList<E> toArray() {
        ArrayList<E> array = new ArrayList<>();
        for (int i=0 ; i < size() ; i++) {
            array.add(tree.get(i).value());
        }
        return array;
    }

    public void printPriorityQueue() {
        System.out.print("Priority Queue: [ ");
        for (int i=0 ; i < size() ; i++) {
            Node node = tree.get(i);
            System.out.print("(" + node.value + "," + node.priority + "), ");
        }
        System.out.println("]");
        return;
    }

    public class Node {
        public Node(E value, P priority, int idx) {
            this.value = value;
            this.priority = priority;
            this.idx = idx;
        }

        E value;
        P priority;
        int idx;

        boolean removed = false;

        void markRemoved()          { removed = true; }
        public E value()            { return value; }
        public P priority()         { return priority; }
        public boolean isValid()    { return !removed; }

        public void changePriority(P newPriority) {
            checkNodeValidity();
            if (compare(newPriority, priority) < 0) {
                priority = newPriority;
                pullUp(idx);
            } else if (compare(newPriority, priority) > 0) {
                priority = newPriority;
                pushDown(idx);
            }
        }

        private void checkNodeValidity() {
            if (removed) {
                throw new IllegalStateException("node is no longer part of heap");
            }
        }

        public void remove() {
            checkNodeValidity();

            if (tree.size() == 1) {
                tree.remove(idx);
                markRemoved();
            }
            if (idx == tree.size() - 1) {
                markRemoved();
                tree.remove(idx);
            } else {
                markRemoved();
                final Node nodeToMoveToThisIdx = tree.remove(tree.size() - 1);
                nodeToMoveToThisIdx.idx = idx;
                tree.set(idx, nodeToMoveToThisIdx);
                if (compare(tree.get(parent(idx)).priority,
                        nodeToMoveToThisIdx.priority) > 0) {
                    pullUp(idx);
                } else {
                    pushDown(idx);
                }
            }
        }
    }
}
