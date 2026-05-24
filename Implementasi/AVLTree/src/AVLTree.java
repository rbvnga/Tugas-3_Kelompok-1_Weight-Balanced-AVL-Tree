import java.util.*;

class Node {
   int key, height;
   Node left, right;
   Node(int d) {
      key = d;
      height = 1;
   }
}

public class AVLTree {
   Node root;
   int rotationCount = 0;

   int height(Node N) {
      if (N == null)
         return 0;
      return N.height;
   }

   int max(int a, int b) {
      return (a > b) ? a : b;
   }

   Node rightRotate(Node y) {
      rotationCount++;
      Node x = y.left;
      Node T2 = x.right;
      x.right = y;
      y.left = T2;
      y.height = max(height(y.left), height(y.right)) + 1;
      x.height = max(height(x.left), height(x.right)) + 1;
      return x;
   }

   Node leftRotate(Node x) {
      rotationCount++;
      Node y = x.right;
      Node T2 = y.left;
      y.left = x;
      x.right = T2;
      x.height = max(height(x.left), height(x.right)) + 1;
      y.height = max(height(y.left), height(y.right)) + 1;
      return y;
   }

   int getBalance(Node N) {
      if (N == null)
         return 0;
      return height(N.left) - height(N.right);
   }

   Node insert(Node node, int key) {
      if (node == null)
         return (new Node(key));
      if (key < node.key)
         node.left = insert(node.left, key);
      else if (key > node.key)
         node.right = insert(node.right, key);
      else
         return node;
      node.height = 1 + max(height(node.left), height(node.right));
      int balance = getBalance(node);
      if (balance > 1 && key < node.left.key)
         return rightRotate(node);
      if (balance < -1 && key > node.right.key)
         return leftRotate(node);
      if (balance > 1 && key > node.left.key) {
         node.left = leftRotate(node.left);
         return rightRotate(node);
      }
      if (balance < -1 && key < node.right.key) {
         node.right = rightRotate(node.right);
         return leftRotate(node);
      }
      return node;
   }

   Node minValueNode(Node node) {
      Node current = node;
      while (current.left != null)
         current = current.left;
      return current;
   }

   Node deleteNode(Node root, int key) {
      if (root == null)
         return root;
      if (key < root.key)
         root.left = deleteNode(root.left, key);
      else if (key > root.key)
         root.right = deleteNode(root.right, key);
      else {
         if ((root.left == null) || (root.right == null)) {
            Node temp = (root.left != null) ? root.left : root.right;
            if (temp == null)
               root = null;
            else
               root = temp;
         } else {
            Node temp = minValueNode(root.right);
            root.key = temp.key;
            root.right = deleteNode(root.right, temp.key);
         }
      }
      if (root == null)
         return root;
      root.height = max(height(root.left), height(root.right)) + 1;
      int balance = getBalance(root);
      if (balance > 1 && getBalance(root.left) >= 0)
         return rightRotate(root);
      if (balance > 1 && getBalance(root.left) < 0) {
         root.left = leftRotate(root.left);
         return rightRotate(root);
      }
      if (balance < -1 && getBalance(root.right) <= 0)
         return leftRotate(root);
      if (balance < -1 && getBalance(root.right) > 0) {
         root.right = rightRotate(root.right);
         return leftRotate(root);
      }
      return root;
   }

   boolean search(Node node, int key) {
      if (node == null) return false;
      if (key == node.key)
         return true;
      if (key < node.key)
         return search(node.left, key);
      return search(node.right, key);
   }

   void printTree(Node root) {
      if (root == null)
         return;
      printTree(root.left);
      System.out.print(root.key + " ");
      printTree(root.right);
   }

   public static void main(String args[]) {
      AVLTree tree = new AVLTree();
      int N = 1000;

      int[] data = new int[N];
      Random rand = new Random(42);
      for (int i = 0; i < N; i++)
         data[i] = rand.nextInt(10000);

      // benchmark insert
      long start = System.nanoTime();
      for (int val : data)
         tree.root = tree.insert(tree.root, val);
      long end = System.nanoTime();

      System.out.println("=== AVL Tree Benchmark ===");
      System.out.println("Insert " + N + " nodes");
      System.out.println("Waktu : " + (end - start) / 1_000_000.0 + " ms");
      System.out.println("Rotasi: " + tree.rotationCount);
      System.out.println("Tinggi: " + tree.height(tree.root));

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
      for (int i = 0; i < 500; i++)
         tree.root = tree.deleteNode(tree.root, data[i]);
      end = System.nanoTime();

      System.out.println("\nDelete 500 nodes");
      System.out.println("Waktu : " + (end - start) / 1_000_000.0 + " ms");
      System.out.println("Rotasi: " + tree.rotationCount);
      System.out.println("Tinggi: " + tree.height(tree.root));

      // print hasil akhir tree
      System.out.print("\nAVL Tree: ");
      tree.printTree(tree.root);
      System.out.println();
   }
}
