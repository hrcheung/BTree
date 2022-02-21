# Btree
Name: Haoran Zhang
Email: zhang.haoran1@northeastern.edu

**LOGIC: when inserting, we are always doing 3 things:** 

1. direct input 

2. split (2 new nodes ; we return a parent node that wait to be combined)
 - create a new leaf to distribute values;
 - create a new parent node to store the mid value
 - store children index in the new parent node
 
3. combine with parent
 - we find that, if the current pointer is a leaf node, this means we are the first level!
 - we need to go back and update to a new root!

what if we are not leaf? This means we need to go down to find children!   ------ we use recursion





**WRONG PART THAT CRACKS THE WHOLE PROGRAM:**

isLeaf():
when the children array is [0,0,0,0,0], the isLeaf() returns false. but actually it should be true. I tried to use for loop
*****************************************
    boolean isLeaf(Node node) {
    for (int value: node.children){
      if (value>0){
        return false;
      }
    }
    return true;
  }
  
*****************************************
it still fails as it warns me NULLPOINTER EXCEPTION (int value can be null)

I also tried to create new split arrays int[] and then use isLeaf, but still failed. I don't know how to let JAVA know "0 is null" in my program :(. 

