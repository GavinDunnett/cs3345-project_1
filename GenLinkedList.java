import java.util.*;

/*
 * This generic class employs nodes to store a value of the specified generic
 * type.
 * 
 * All the methods in this class operate on the object making the call, none are
 * static. In addition, the implementation of each method is optimized for
 * efficeincy.
 * 
 * Where appropriate, parameters passed to methods are checked and the
 * corresponding exceptions are thrown.
 * 
 * This class uses a linked list which is singly-linked. It does not use
 * sentinel nodes.
 * 
 *@author Gavin John Dunnett
 */

public class GenLinkedList<T extends Comparable<? super T>> {
    /**
     * The number of nodes in the list.
     */
    private int size;
    /**
     * The list's front node.
     */
    private Node<T> frontNode;

    public GenLinkedList() {
        clear();
    }

    /**
     * Task a. Receives an item to add as a parameter and adds to the front of the
     * list.
     */
    public void addFront(T item) {
        System.out.println("    Task a. addFront(" + item + ")");
        addNode(0, item);
    }

    /**
     * Task b. Receives an item to add as a parameter, and adds to the end of the
     * list.
     */
    public void addEnd(T item) {
        System.out.println("    Task b. addEnd(" + item + ")");
        addNode(size, item);
    }

    /**
     * Task c. Removes a node from the front of the list.
     */
    public void removeFront() {
        System.out.println("    Task c. removeFront()");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        removeRange(0, 1);
    }

    /**
     * Task d. Removes a node from the end of the list.
     */
    public void removeEnd() {
        System.out.println("    Task d. removeEnd()");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        removeRange(size - 1, size);
    }

    /**
     * Task e. Receives an index and an item as parameters, sets the item at that
     * index, provided it is within the current size.
     */
    public void set(int index, T item) {
        System.out.println("    Task e. set(" + index + "," + item + ")");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        Node<T> quarry = getNode(index);
        quarry.item = item;
    }

    /**
     * Task f. Receives a index as a parameter, returns the item at that index,
     * provided it is within the current list.
     */
    public T get(int index) {
        System.out.println("    Task f. get(" + index + ")");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        return getNode(index).item;
    }

    /**
     * Task g. Receives two index positions as parameters, and swaps the nodes at
     * these positions, provided both positions are within the current size.
     */
    public void swap(int a, int b) {
        System.out.println("    Task g. swap(" + a + "," + b + ")");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        if (!(usedIndex(a) && usedIndex(b)))
            throw new IndexOutOfBoundsException("Specified index not valid.");
        if (a > b) { // need indices in ascending order for a efficent list traverse
            a = a + b;
            b = a - b; // b=(a+b)-b
            a = a - b; // a=a-(a-b)
        }
        Node<T> Aprev;
        if (a == 0) // special case: frontNode being swapped, create a dummy preceding node
            Aprev = new Node<T>(null, frontNode);
        else
            Aprev = getNode(a - 1, frontNode);
        Node<T> A = Aprev.next;
        Node<T> Asubs = A.next;
        Node<T> Bprev = getNode(b - a - 1, A);
        Node<T> B = Bprev.next;
        Node<T> Bsubs = B.next;
        if (a == 0 && a + 1 != b) { // special case: frontNode being swappad with non-neighbor
            frontNode = B;
            Bprev.next = A;
            frontNode.next = Asubs;
            A.next = Bsubs;
        } else if (a == 0 && a + 1 == b) { // special case: 2 front nodes being swapped
            frontNode = B;
            frontNode.next = A;
            A.next = Bsubs;
        } else if (a + 1 == b) { // special case: nodes are adjacent
            Aprev.next = B;
            B.next = A;
            A.next = Bsubs;
        } else {
            Aprev.next = B;
            Bprev.next = A;
            A.next = Bsubs;
            B.next = Asubs;
        }
    }

    /**
     * Task h. Receives an integer as a parameter, and shifts the list forward or
     * backward this number of nodes, provided it is within the current size.
     */
    public void shift(int variation) {
        System.out.println("    Task h. shift(" + variation + ")");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        if (!(-size <= variation && variation <= size))
            throw new IndexOutOfBoundsException("Variation must be within the current size.");
        if (variation == size || variation == -size) {
            // In this case, there is no point trying to shift the nodes since they would
            // only end up in the same positions. We are striving for efficient
            // implementations.
        } else if (variation > 0) { // shift to right (+variation)
            for (int i = 0; i < variation; i++) {
                addFront(get(size - 1));
                removeEnd();
            }
        } else if (variation < 0) { // shift to left (-variation)
            for (int i = 0; i > variation; i--) {
                addEnd(get(0));
                removeFront();
            }
        }
    }

    /**
     * Task i. Receives a value of the generic type as a parameter and removes all
     * occurrences of this value from the list.
     */
    public void removeMatching(T target) {
        System.out.println("    Task i. removeMatching(" + target + ")");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        for (int i = 0; i < size; i++) { // traverse the list
            if (getNode(i).item.compareTo(target) == 0) {
                removeRange(i, i + 1); // removeRange does size--
                i--; // index of node to resume traverse
            }
        }
    }

    /**
     * Task j. Receives an index position and number of items as parameters, and
     * removes items beginning at the index position for the number of items
     * specified, provided the index position is within the size and together with
     * the number of items does not exceed the size.
     */
    public void erase(int index, int extent) {
        System.out.println("    Task j. erase(" + index + "," + extent + ")");
        if (size <= 0)
            throw new NoSuchElementException("List is empty.");
        removeRange(index, index + extent);
    }

    /**
     * Task k. Receives a generic List (a Java List) and an index position as
     * parameters, and copies each value of the passed list into the current list
     * starting at the index position, provided the index position does not exceed
     * the size.
     */
    public void insertList(int index, List<? extends T> c) {
        System.out.println("    Task k. insertList(" + index + "," + c.toString() + ")");
        Collections.reverse(c);
        for (T item : c)
            addNode(index, item);
    }

    /**
     * Removes the node(s) in the specified interval. e.g. calling removeRange(1,3)
     * removes the nodes at indices [1,3) which is nodes 1 and 2. To remove the list
     * frontNode use (0,1). To remove the endNode use (size - 1, size)
     */
    private void removeRange(int first, int subsequent) {
        if (size <= 0)
            throw new NoSuchElementException("removeRange() called on an empty list.");
        else if (first < 0 || first > size - 1 || subsequent < 0 || subsequent > size)
            throw new IndexOutOfBoundsException("removeRange() start/finsh out of bounds.");
        else if (first >= subsequent)
            throw new IllegalArgumentException("removeRange() start index is greater or equals finish index.");
        else {
            Node<T> priorNode = new Node<T>(null, frontNode); // node prior to the first node being removed
            for (int i = 0; i < first; i++)
                priorNode = priorNode.next;
            Node<T> subsequentNode = priorNode;
            for (int j = first; j < subsequent + 1; j++) // node subsequent to the last node being removed
                subsequentNode = subsequentNode.next;
            if (priorNode.next == frontNode) // special case: frontNode is part of the removal
                frontNode = subsequentNode;
            else if (priorNode.next == frontNode && subsequentNode == null) // special case: all nodes removed
                clear();
            else
                priorNode.next = subsequentNode; // regular case: internal nodes being removed
            size = size - (subsequent - first); // reduce the list size
        }
    }

    /**
     * Receives an index and an item as parameters then adds a new node at that
     * index. If the list is empty then a node is added at index 0. If a node is
     * added at index = size a node is added to the end of the list.
     */
    private void addNode(int index, T item) {
        if (!(0 <= index && index <= size))
            throw new IndexOutOfBoundsException("Specified index not valid.");
        else if (size == 0) { // special case: add to empty list
            frontNode.item = item;
            size++;
        } else if (index == size) { // special case: add to the end
            Node<T> endNode = getNode(index - 1);
            endNode.next = new Node<T>(item, null);
            size++;
        } else if (index == 0) { // special case: add to the front
            Node<T> orginalFrontNode = getNode(index);
            frontNode = new Node<T>(item, orginalFrontNode);
            size++;
        } else if (0 < index && index < size) { // add to the interior
            Node<T> priorNode = getNode(index - 1);
            Node<T> newNode = new Node<T>(item, priorNode.next);
            priorNode.next = newNode;
            size++;
        }
    }

    /**
     * Receives a index as a parameter, returns the Node at that index, provided it
     * is within the current list.
     */
    private Node<T> getNode(int index) {
        if (!usedIndex(index))
            throw new IndexOutOfBoundsException("Specified index not valid.");
        Node<T> target = frontNode;
        for (int i = 0; i < index; i++)
            target = target.next;
        return target;
    }

    /**
     * Receives a node and a positive integer. Returns the node located at that
     * distance after the specified node.
     */
    private Node<T> getNode(int distance, Node<T> x) {
        if (distance < 0)
            throw new IllegalArgumentException("Distance can not be negative.");
        while (distance > 0) {
            x = x.next;
            distance--;
            if (x == null)
                throw new IndexOutOfBoundsException("Distance not within current size.");
        }
        return x;
    }

    /**
     * Nulls the front node's contents and sets the list's size to 0;
     */
    public void clear() {
        frontNode = new Node<T>(null, null);
        size = 0;
    }

    /**
     * Test if the specified index is an index of an existing element.
     */
    private boolean usedIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * This static nested class forms the nodes in the linked list. Static nested
     * classes do not have access to members of the enclosing class.
     */
    private static class Node<T extends Comparable<? super T>> {
        T item;
        Node<T> next;
    
        Node(T item, Node<T> next) {
            this.item = item;
            this.next = next;
        }
    }

    /**
     * Return all the linked list's items.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node<T> x = frontNode; x != null; x = x.next)
            sb.append(x.item.toString() + " ");
        return sb.toString();
    }

    public static void main(String[] args) {
        GenLinkedList<Character> lst = new GenLinkedList<>();
        List<Character> letters = Arrays.asList('X', 'E', 'D', 'C', 'B', 'A', 'X', 'X');
        System.out.println("    ****************************");
        System.out.println("    Demonstration of the methods");
        System.out.println("    ****************************");
        System.err.println();
        for (Character c : letters)
            lst.addFront(c);
        System.out.println(lst);
        lst.addEnd('X');
        System.out.println(lst);
        lst.removeFront();
        System.out.println(lst);
        lst.removeEnd();
        System.out.println(lst);
        lst.set(5, 'X');
        System.out.println(lst);
        System.out.println(lst.get(0));
        System.out.println(lst.get(4));
        lst.swap(0, 1);
        System.out.println(lst);
        lst.swap(4, 6);
        System.out.println(lst);
        lst.shift(2);
        System.out.println(lst);
        lst.removeMatching('X');
        System.out.println(lst);
        lst.shift(-1);
        System.out.println(lst);
        lst.erase(1, 2);
        System.out.println(lst);
        List<Character> cList = Arrays.asList('M', 'A', 'Z', 'E');
        lst.insertList(1, cList);
        System.out.println(lst);
        lst.erase(5, 1);
        System.out.println(lst);
        lst.erase(0, 1);
        System.out.println(lst);
    }
}