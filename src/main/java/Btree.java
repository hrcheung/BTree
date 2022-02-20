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
  private int overflowValue; //used to store overflowed value

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
      else{ // no space left
        int new_node_pointer = initNode();  //create a new node and return its pointer
        int split=NODESIZE/2; //split denotes how many values put in the left split node
        //distribute values

        //overflow value is the one in the middle
        this.overflowValue=this.nodes[pointer].values[split];
        //right split node is the new node
        for (int j=0;j<=split-1;j++){ //let the first several elements
          this.nodes[new_node_pointer].values[j]=this.nodes[pointer].values[this.nodes[pointer].size-split+j];// equal to the other part of split values;
        }

        //left split node is the origin node.
        for (int i=split;i<=this.nodes[pointer].size-1;i++){  //keep the first several elements;
          this.nodes[pointer].values[i]=0; //make the rest to 0;
        }

        //now the middle value needs to go overflow --
        // -- if pointer == root pointer, we create a new root node as the parent node
        // -- else, we insert the middle to current pointer's parent node

        if (pointer==this.root) {
          int new_root_pointer = initNode(); //initialize a new root
          this.nodes[new_root_pointer].values[0]=this.overflowValue;//the root value is overflowed value
          this.nodes[new_root_pointer].children[0]=pointer; //first children is original left part
          this.nodes[new_root_pointer].children[1]=new_node_pointer; //second children is original right part
          return -1;
        }
        else{ //if pointer is not a root, insert the overflowed value to current pointer's parent node
//          this.nodeInsert(this.overflowValue,)
          System.out.println("need to find current pointer's parent node");
          return -4;
        }
      }
    }
    else {//the current node is not leaf node;
      for (int child_node_pointer : this.nodes[pointer].children) { //find the child node for the current node
        int child = nodeInsert(value, child_node_pointer); //find child value
        if ((child==-2)||(child==-1)){ //value already exists or everything is done
          return pointer; //return this node pointer
        }
      }
      if (this.nodes[pointer].size<NODESIZE){ //there is still space in current node
        //size now is not only the value index but also the pointer index
        int index = this.nodes[pointer].size;
        this.nodes[pointer].children[index]=pointer; //insert the new child pointer into teh current node
        this.nodes[pointer].values[index]=value; //insert the child's first value into the current node;
        return -1;
      }

      else { //there is no space left

        int new_node_pointer = initNode();//create a new node

        //DISTRIBUTE values and child pointers
        //given NODESIZE=5, first 2 values go with origin pointer; rest 3 go with new node pointer
        for (int j=0;j<=NODESIZE/2;j++){ //update new pointer node - j takes 3 values, which are 0,1,2
          this.nodes[new_node_pointer].values[j]=this.nodes[pointer].values[j+2]; //should be origin node values' index [2,3,4]
          this.nodes[new_node_pointer].children[j]=createLeaf(); //using createLeaf, put new leaf pointer into child leaf's array
        }

        for (int i=0;i<NODESIZE;i++){ //update origin pointer node
          if (i<NODESIZE/2) {
            continue;
          }
          else{ //i = 2,3,4
            this.nodes[pointer].values[i]=0;  //initialize values
            this.nodes[pointer].children[i]=0;//initialize children pointers
          }
        }

        if (pointer!=this.root){//A way to determine if a node is the root::::: root should be 0
          return new_node_pointer;
        }

        else{ //if the node is the root node
          int new_rootNode_pointer=iniNode();
          this.root=new_rootNode_pointer;
          //how to do initialize???
        }
      }
      return -1;
    }
  }
  private void display(int node){ //

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
