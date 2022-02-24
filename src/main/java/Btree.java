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
  public int root;


  /* Number of currently used values. */
  private int cntValues;
  private Node potentialParent; //used to store overflowed parent


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
          // we come to this only if we finish the Node potentialParent!
      int new_root_pointer=initNode(); //get a new pointer for this new root
      this.nodes[new_root_pointer]=this.potentialParent; //by doing this, values and children has been finished
      this.root=new_root_pointer;   //update root pointer
      cntValues+=1;
      System.out.println("Update Root!");
      //this.display(this.root);
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
          return recursionValue;
        }
      }
      //what if we go to the rightest handside of current pointer values?  -- i doubt it happens; but in Java we need to write a line to avoid exception
      int recursionValue=nodeInsert(value,this.nodes[pointer].children[this.nodes[pointer].size]);//we go to the rightest children (this will not happen right? becaus we are not in the leaf node)
      if (recursionValue>=0){ //not -1, not -2, but comes as a parent needed to be combined
        return combineWithParent(pointer,this.potentialParent);
      }
      else{//if -1 or -2, we finish everything!
        return recursionValue;
      }
    }
  }

  private int combineWithParent(int pointer, Node potentialParent) { // now the pointer is a higher level (comparing to potential Parent node's children)
    //there are 2 cases in combination
    //1. current pointer node has enough room for potential parent
    //   - just put potential Parent value & children
    //2. not enough room, need to do split and combine again! (using recursion)
    //
    // note: we return int, which denote the potential value as in Insert function
    //       we strictly follow putting together - see if exceeding size - then decide whether split
    this.nodes[pointer].size+=1;//update size
    if (this.nodes[pointer].size>NODESIZE){ //if
      this.potentialParent=splitNode(pointer);
      return 1;
    }
    else{
      int value_to_insert = potentialParent.values[0]; //because we now merge and we know enough room, just need value and children index
      int children1Index= potentialParent.children[0];
      int children2Index=potentialParent.children[1];

      //2,4, 5 -> 2,3,4,5, where 3 is the value to be inserted
      this.nodes[pointer].values[this.nodes[pointer].size-1]=value_to_insert;
      Arrays.stream(this.nodes[pointer].values).sorted(); //insert and sort

      // new children1 index should be value_to_insert position; children 2 index is the position +1
      int target=0;
      for (int i=0;i<=this.nodes[pointer].size-1;i++){
        if (this.nodes[pointer].values[i]==value_to_insert){ //we find the new value index
          target=i;
          break;
        }
      }
      this.nodes[pointer].children[target]=children1Index; //we update children1
      this.nodes[pointer].children[target+1]=children2Index; //update children2
      return -1;
    }
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
    int overflowValue=this.nodes[pointer].values[split];
    //right split node is the new node

    System.arraycopy(this.nodes[pointer].values,this.nodes[pointer].size-split,this.nodes[new_childnode_pointer].values,0,split);
    this.nodes[new_childnode_pointer].size=split; //remember to update size!

    //left split node is the origin node.
    int[] leftSplitValues= new int[NODESIZE]; //we had to create a new int[], as isLeaf use == null to determine, but 0 != null; null cannot be compared
    System.arraycopy(this.nodes[pointer].values,0,leftSplitValues,0,split);
    this.nodes[pointer].values = leftSplitValues;
//    for (int i=split;i<=this.nodes[pointer].size-1;i++){  //keep the first several elements;
//      this.nodes[pointer].values[i]= 0; //make the rest to 0;
//    }
    this.nodes[pointer].size=split; //remember to update size!

    //now I need a new parent node. and I should return this node in this splitNode function.
    Node potential_parentNode = new Node();
    potential_parentNode.size=1; //only 1 overflowed value
    potential_parentNode.values=new int[this.NODESIZE]; //initialize a new value array
    potential_parentNode.values[0]=overflowValue; //parent value is the overflowed value
    potential_parentNode.children=new int[this.NODESIZE+1]; //initialize a new children array
    potential_parentNode.children[0]=pointer; //left pointer
    potential_parentNode.children[1]=new_childnode_pointer; //right pointer
    return potential_parentNode; //note: this is a potential parent node - may be merged with existing parent later

  }

  public void display(int node){ //display structures under this node

    //recursion

    //break condition
    if (this.isLeaf(this.nodes[node])){ //directly display the values array because no children

      String valuesStr = "";
      for (int i=0;i<=this.nodes[node].size-1;i++){
        if (this.nodes[node].values[i]==0){
          valuesStr+="0";
          valuesStr+=",";
        }
        else{
          valuesStr+=this.nodes[node].values[i];
          valuesStr+=",";
        }
      }
      System.out.println("this is a leaf node "+node+ " with values \n "+'['+valuesStr);
    }

    //use recursion
    else{
      //first get this parent node's values
      String valuesStr = "";
      for (int i=0;i<=this.nodes[node].size-1;i++){
        valuesStr+=this.nodes[node].values[i];
        valuesStr+=",";
      }
      System.out.println("this is a parent node "+node+ " with values \n "+'['+valuesStr);

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
    return node.children==null;
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
