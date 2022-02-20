/*
 * CS7280 Special Topics in Database Management
 * Project 1: B-tree implementation.
 *
 * You need to code for the following functions in this program
 *   1. Lookup(int value) -> nodeLookup(int value, int node)
 *   2. Insert(int value) -> nodeInsert(int value, int node)
 *   3. Display(int node)
 *
 */

import java.util.Arrays;

final class Btree {

  /* Size of Node. */
  private static final int NODESIZE = 5;

  /* Node array, initialized with length = 1. i.e. root node */
  private Node[] nodes = new Node[1];

  /* Number of currently used nodes. */
  private int cntNodes;

  /* Pointer to the root node. */
  private int root;

  /* Number of currently used values. */
  private int cntValues;
  private Node potentialParent; //used to store overflowed parent
  private int overflowValue;

  /*
   * B+ tree Constructor.
   */
  public Btree() {
//    root = iniNode();
//    System.out.println("put root in");
    //nodes[root].children[0] = createLeaf(); //null pointer exception;

    //root=iniNode(); //not use iniNode. because in this case, children and values parameters will not be created.
    root=createLeaf(); //use createLeaf, which generates children and values arrays.
  }

  private int iniNode() {
    return 0;
  }

  /*********** B tree functions for Public ******************/

  /*
   * Lookup(int value)
   *   - True if the value was found.
   */
  public boolean Lookup(int value) {return nodeLookup(value, root);}

  /*
   * Insert(int value)
   *    - If -1 is returned, the value is inserted and increase cntValues.
   *    - If -2 is returned, the value already exists.
   */
  public void Insert(int value) {
    int potentialValue=nodeInsert(value,root);
    //System.out.println("potential Value is "+potentialValue);

    if(potentialValue == -1) {
      cntValues++;
      System.out.println("insert succeed");
    }
    else if (potentialValue==-2){ //for testing purpose
      System.out.println("nothing was inserted");
    }
    else{ //potentialValue ==1, root node is full, need to be splitted.
      //here we only deal with overflow associated wit root --- all other overflow under root level should be solved already
      System.out.println("root node is full, need to be splitted");
//      int new_root_pointer = initNode(); //initialize a new root
//      this.nodes[new_root_pointer].values[0]=this.nodes[]
    }
  }


  /*
   * CntValues()
   *    - Returns the number of used values.
   */
  public int CntValues() {
    return cntValues;
  }

  /*********** B-tree functions for Internal  ******************/

  /*
   * nodeLookup(int value, int pointer)
   *    - True if the value was found in the specified node.
   *
   */
  private boolean nodeLookup(int value, int pointer) {
    // To be coded .................
    if (isLeaf(this.nodes[pointer])){ //if the current node is a leaf node
      for (int i=0;i<=this.nodes[pointer].size-1;i++){ //access every value in used values
        if (this.nodes[pointer].values[i] == value){ //if we find it
          return true; //return true
        }
      }
      return false; //we don't find it, return false
    }
    //else not a leaf
    for (int j=0;j<=this.nodes[pointer].children.length-1;j++){ //not a leaf, so we access its every child via pointer.
      return nodeLookup(value,this.nodes[pointer].children[j]);//use recursion to look up its children
    }
    return false;
  }


  /*
   * nodeInsert(int value, int pointer)
   *    - -2 if the value already exists in the specified node
   *    - -1 if the value is inserted into the node or
   *            something else if the parent node has to be restructured
   */
  private int nodeInsert(int value, int pointer) {
    //
    //
    // To be coded .................
    //when the value comes from Insert, we are always inserting into root pointer.


    if (this.nodes[pointer].size==0){ //insert first root pointer into nodes: null exception
      this.nodes[pointer].values[0]=value; //directly put the value into values array
      this.nodes[pointer].size+=1;
      return -1; //
    }
    if (isLeaf(this.nodes[pointer])){ //if the current node is a leaf node:

      for (int i=0;i<=this.nodes[pointer].size-1;i++){
        if (this.nodes[pointer].values[i]==value){ //if the value is found in the leaf node
          System.out.println("already exists");
          return -2;//shows nothing was inserted
        }
      }

      if(this.nodes[pointer].size<NODESIZE){//there are space left in this leaf node
//          System.out.println("index = ");
//          System.out.println(this.nodes[pointer].size-1);
        this.nodes[pointer].values[this.nodes[pointer].size]=value;//put value into the values array
        Arrays.stream(this.nodes[pointer].values).sorted(); //sort the values array
        this.nodes[pointer].size+=1; //size increased by 1
        return -1; //everything is done
      }
      else{ // no space left  ---- note: now we are on the leaf level
        //we need to
        //1. split the current node  --- return a parent node containning following children
        //   - create a new leaf to distribute values;
        //   - create a new parent node to store the mid value
        //   - store children index in the new parent node
        //2. combine with parent
        //   we find that, if the current pointer is a leaf node, this means we are the first level!
        //   we need to go back and update to a new root!
        //
        //   what if we are not leaf? This means we need to go down to find children!

        //comes back with a parent node! this parent node should contains 2 children (finishing distributed values)
        this.potentialParent=splitNode(pointer);
        return 1; //1 denotes needing to combine with up level parent
      }
    }
    else {//the current node is not leaf node; we use recursion to go deeper -
      for (int i=0; i<= this.nodes[pointer].size-1;i++){ //find which child to go to
        if (this.nodes[pointer].values[i]>value){//because values in children value array are increasing, if we find a big value, we need to recursion to its left lower level
          int recursionValue = nodeInsert(value,this.nodes[pointer].children[i]);//here the childValue is actually a child pointer!
        }
      }
      //what if we go to the rightest handside of current pointer values?  -- i doubt it happens; but in Java we need to write a line to avoid exception
      int recursionValue=nodeInsert(value,this.nodes[pointer].children[-1]);//we go to the rightest children (this will not happen right? becaus we are not in the leaf node)
      if (recursionValue>=0){ //not -1, not -2, but comes as a parent needed to be combined
        return combineWithParent(pointer,this.potentialParent);
      }
      else{//if -1 or -2, we finish everything!
        return recursionValue;
      }
    }
  }

  private int combineWithParent(int pointer, Node potentialParent) { // now the pointer is a higher level (comparing to potential Parent node's children)

  }

  private Node splitNode(int pointer) {
    //   - create a new leaf to distribute values;
    //   - create a new parent node to store the mid value
    //   - store children index in the new parent node

    // be careful: we don't return pointer, because it is possible we don't need a new pointer - this new Parent node might be merged with existing node
    int new_childnode_pointer = initNode();  //create a new node and return its pointer
    int split=NODESIZE/2; //split denotes how many values put in the left split node
    //distribute values

    //overflow value is the one in the middle
    this.overflowValue=this.nodes[pointer].values[split];
    //right split node is the new node
    for (int j=0;j<=split-1;j++){ //let the first several elements
      this.nodes[new_childnode_pointer].values[j]=this.nodes[pointer].values[this.nodes[pointer].size-split+j];// equal to the other part of split values;
    }

    //left split node is the origin node.
    for (int i=split;i<=this.nodes[pointer].size-1;i++){  //keep the first several elements;
      this.nodes[pointer].values[i]=0; //make the rest to 0;
    }

    //now I need a new parent node. and I should return this node in this splitNode function.
    Node new_parentNode = new Node();
    new_parentNode.values=new int[this.NODESIZE];
  }

  public void display(int node){ //display structures under this node
    if (this.isLeaf(this.nodes[node])){

      String valuesStr = "";
      for (int i=0;i<=this.nodes[node].size-1;i++){
        valuesStr+=this.nodes[node].values[i];
        valuesStr+=",";
      }
      System.out.println("this is a leaf node with values"+valuesStr);
    }
    else{
      System.out.println("this is a parent node, please see below for its values and leaf values");
      //first get its values
      String valuesStr = "";
      for (int i=0;i<=this.nodes[node].size-1;i++){
        valuesStr+=this.nodes[node].values[i];
        valuesStr+=",";
      }

      System.out.println("first, this node values are"+valuesStr);

      //then get its leaf's values
      for (int j=0;j<=this.nodes[node].children.length-1;j++){
        if (this.nodes[node].children[j]!=0){
          display(j);//use recursion to display
        }
      }

    }
  }

//  private boolean contains(int[] values,int value) {
//    for (int i=0;i<=values.length-1;i++){
//      if (value==values[i]){
//        return true;
//      }
//    }
//    return false;
//  }



  /*********** Functions for accessing node  ******************/

  /*
   * isLeaf(Node node)
   *    - True if the specified node is a leaf node.
   *         (Leaf node -> a missing children)
   */
  boolean isLeaf(Node node) {
    return node.children == null;
  }

  /*
   * initNode(): Initialize a new node and returns the pointer.
   *    - return node pointer ------ 返回的是指针！
   */
  int initNode() {
    Node node = new Node();
    node.values = new int[NODESIZE];
    node.children =  new int[NODESIZE + 1];

    checkSize();
    nodes[cntNodes] = node;
    return cntNodes++;
  }

  /*
   * createLeaf(): Creates a new leaf node and returns the pointer.
   *    - return node pointer
   */
  int createLeaf() {
    Node node = new Node();
    node.values = new int[NODESIZE];

    checkSize();
    nodes[cntNodes] = node;
    return cntNodes++;
  }

  /*
   * checkSize(): Resizes the node array if necessary.    --- what is this?
   */
  private void checkSize() {
    if(cntNodes == nodes.length) {
      Node[] tmp = new Node[cntNodes << 1];
      System.arraycopy(nodes, 0, tmp, 0, cntNodes);
      nodes = tmp;
    }
  }
}

/*
 * Node data structure.
 *   - This is the simplest structure for nodes used in B-tree
 *   - This will be used for both internal and leaf nodes.
 */
final class Node {
  /* Node Values (Leaf Values / Key Values for the children nodes).  */
  int[] values;

  /* Node Array, pointing to the children nodes.
   * This array is not initialized for leaf nodes.
   */
  int[] children;

  /* Number of entries
   * (Rule in B Trees:  d <= size <= 2 * d).
   */
  int size;
}
