import java.util.*;

class WBTNode {
   int key, size;
   WBTNode left, right;
   WBTNode(int d) {
      key = d;
      size = 1;
   }
}

public class WeightBalancedAVLTree {
   WBTNode root;
   int rotationCount = 0;

   // parameter dari paper Hirai & Yamamoto (2011)
   static final int DELTA = 3;
   static final int GAMMA = 2;

   int size(WBTNode n) {
      if (n == null)
         return 0;
      return n.size;
   }

   void updateSize(WBTNode n) {
      n.size = size(n.left) + size(n.right) + 1;
   }

   // isBalanced: delta * (size_a + 1) >= (size_b + 1)
   boolean isBalanced(WBTNode a, WBTNode b) {
      return DELTA * (size(a) + 1) >= (size(b) + 1);
   }

   // isSingle: (size_a + 1) < gamma * (size_b + 1)
   boolean isSingle(WBTNode a, WBTNode b) {
      return (size(a) + 1) < GAMMA * (size(b) + 1);
   }

   WBTNode rotateRight(WBTNode y) {
      rotationCount++;
      WBTNode x = y.left;
      WBTNode T2 = x.right;
      x.right = y;
      y.left = T2;
      updateSize(y);
      updateSize(x);
      return x;
   }

   WBTNode rotateLeft(WBTNode x) {
      rotationCount++;
      WBTNode y = x.right;
      WBTNode T2 = y.left;
      y.left = x;
      x.right = T2;
      updateSize(x);
      updateSize(y);
      return y;
   }

   // single/double rotation mengikuti paper
   WBTNode balanceLeft(int key, WBTNode l, WBTNode r) {
      if (!isBalanced(l, r)) {
         // perlu rotasi kiri
         if (isSingle(r.left, r.right)) {
            // single left rotation
            WBTNode newLeft = new WBTNode(key);
            newLeft.left = l;
            newLeft.right = r.left;
            updateSize(newLeft);
            WBTNode result = new WBTNode(r.key);
            result.left = newLeft;
            result.right = r.right;
            updateSize(result);
            rotationCount++;
            return result;
         } else {
            // double left rotation
            WBTNode rl = r.left;
            WBTNode newLeft = new WBTNode(key);
            newLeft.left = l;
            newLeft.right = rl.left;
            updateSize(newLeft);
            WBTNode newRight = new WBTNode(r.key);
            newRight.left = rl.right;
            newRight.right = r.right;
            updateSize(newRight);
            WBTNode result = new WBTNode(rl.key);
            result.left = newLeft;
            result.right = newRight;
            updateSize(result);
            rotationCount++;
            return result;
         }
      }
      WBTNode node = new WBTNode(key);
      node.left = l;
      node.right = r;
      updateSize(node);
      return node;
   }

   WBTNode balanceRight(int key, WBTNode l, WBTNode r) {
      if (!isBalanced(r, l)) {
         // perlu rotasi kanan
         if (isSingle(l.right, l.left)) {
            // single right rotation
            WBTNode newRight = new WBTNode(key);
            newRight.left = l.right;
            newRight.right = r;
            updateSize(newRight);
            WBTNode result = new WBTNode(l.key);
            result.left = l.left;
            result.right = newRight;
            updateSize(result);
            rotationCount++;
            return result;
         } else {
            // double right rotation
            WBTNode lr = l.right;
            WBTNode newLeft = new WBTNode(l.key);
            newLeft.left = l.left;
            newLeft.right = lr.left;
            updateSize(newLeft);
            WBTNode newRight = new WBTNode(key);
            newRight.left = lr.right;
            newRight.right = r;
            updateSize(newRight);
            WBTNode result = new WBTNode(lr.key);
            result.left = newLeft;
            result.right = newRight;
            updateSize(result);
            rotationCount++;
            return result;
         }
      }
      WBTNode node = new WBTNode(key);
      node.left = l;
      node.right = r;
      updateSize(node);
      return node;
   }

   WBTNode insert(WBTNode node, int key) {
      if (node == null)
         return new WBTNode(key);
      if (key < node.key)
         return balanceLeft(node.key, insert(node.left, key), node.right);
      else if (key > node.key)
         return balanceRight(node.key, node.left, insert(node.right, key));
      else
         return node;
   }

   WBTNode minValueNode(WBTNode node) {
      WBTNode current = node;
      while (current.left != null)
         current = current.left;
      return current;
   }

   WBTNode deleteNode(WBTNode node, int key) {

      if (node == null) return null;
      if (key < node.key) return balanceLeft(node.key, deleteNode(node.left, key), node.right);
      
      else if (key > node.key) return balanceRight(node.key, node.left, deleteNode(node.right, key));
      else {

         if (node.left == null) return node.right;
         if (node.right == null) return node.left;
         
         WBTNode temp = minValueNode(node.right);
         
         return balanceRight(temp.key, node.left, deleteNode(node.right, temp.key));
      }
   }

   boolean search(WBTNode node, int key) {
      if (node == null)return false;
      
      if (key == node.key)return true;

      if (key < node.key) return search(node.left, key);

      return search(node.right, key);
   }

   void printTree(WBTNode root) {

      if (root == null)return;

      printTree(root.left);
      
      System.out.print(root.key + " ");
      printTree(root.right);
   }

   public static void main(String args[]) {
     
      WeightBalancedAVLTree tree = new WeightBalancedAVLTree();
      int N = 10;

      int[] data = new int[N];
      Random rand = new Random(42);
      for (int i = 0; i < N; i++)
         data[i] = rand.nextInt(10000);

      // benchmark insert
      long start = System.nanoTime();
      for (int val : data)
         tree.root = tree.insert(tree.root, val);
      long end = System.nanoTime();

      System.out.println("=== Weight-Balanced AVL Tree Benchmark ===");
      System.out.println("Insert " + N + " nodes");
      System.out.println("Waktu : " + (end - start) / 1_000_000.0 + " ms");
      System.out.println("Rotasi: " + tree.rotationCount);
      System.out.println("Tinggi: " + tree.size(tree.root));

      // benchmark search
      start = System.nanoTime();
      for (int val : data)
         tree.search(tree.root, val);
      end = System.nanoTime();

      System.out.println("\nSearch " + N + " nodes");
      System.out.println("Waktu : " + (end - start) / 1_000_000.0 + " ms");

      // benchmark delete
      tree.rotationCount = 0;
      start = System.nanoTime();
      for (int i = 0; i < N/2; i++)
         tree.root = tree.deleteNode(tree.root, data[i]);
      end = System.nanoTime();

      System.out.println("\nDelete" + N/2 + " nodes");
      System.out.println("Waktu : " + (end - start) / 1_000_000.0 + " ms");
      System.out.println("Rotasi: " + tree.rotationCount);
      System.out.println("Size  : " + tree.size(tree.root));

      // print hasil akhir tree
      System.out.print("\nWBT Tree: ");
      tree.printTree(tree.root);
      System.out.println();
   }
}