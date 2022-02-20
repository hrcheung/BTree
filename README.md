# Btree
Name: Haoran Zhang
Email: zhang.haoran1@northeastern.edu

when inserting, we are always doing 3 things:

1. direct input 

2. split (2 new nodes ; we return a parent node that wait to be combined)
 - create a new leaf to distribute values;
 - create a new parent node to store the mid value
 - store children index in the new parent node
 
3. combine with parent
 - we find that, if the current pointer is a leaf node, this means we are the first level!
 - we need to go back and update to a new root!

what if we are not leaf? This means we need to go down to find children!   ------ we use recursion

