# Tugas-3_Kelompok-1_Weight-Balanced-AVL-Tree
| Nama Anggota | NRP |
| :--- | :---|
| Putri Permata Sabila | 5027251047 |
| Revalinda Bunga Nayla Laksono | 5027251011 |
| Nathania Tiara Wahyudi | 5027251089 |
| Jude Athala Yazid Sari| 5027251098 |
| Muhammad Ridwan | 5027251113 |


## Problem Statement

Pada Binary Search Tree (BST) biasa, performa operasi pencarian, penambahan, dan penghapusan sangat bergantung pada bentuk tree-nya. Jika data dimasukkan secara terurut, tree akan condong ke satu sisi dan menyerupai linked list, sehingga kompleksitas yang seharusnya O(log n) bisa terdegradasi menjadi O(n).

AVL Tree mengatasi hal ini dengan menjaga keseimbangan berdasarkan **tinggi (height)** — selisih tinggi antara subtree kiri dan kanan setiap node tidak boleh lebih dari 1. Namun pendekatan ini tidak mempertimbangkan distribusi jumlah node di tiap sisi, sehingga dalam kondisi tertentu rotasi yang dilakukan bisa lebih banyak dari yang sebenarnya diperlukan.

**Weight-Balanced Tree** merupakan modifikasi dari AVL Tree yang mengganti kriteria keseimbangan dari tinggi menjadi **berat (weight)**, yaitu jumlah node pada setiap subtree. Implementasi ini menggunakan parameter δ = 3 dan γ = 2 sesuai paper Hirai & Yamamoto (2011), yang menentukan kapan suatu subtree dianggap tidak seimbang dan jenis rotasi apa yang perlu dilakukan.

Proyek ini mengimplementasikan kedua struktur tersebut dan membandingkan performanya, khususnya dari sisi jumlah rotasi dan tinggi tree yang dihasilkan.


## Struktur Tree dan Algoritma

### AVL Tree

#### Struktur Node

Setiap node pada AVL Tree menyimpan tiga informasi utama: `key` sebagai nilai data, `height` sebagai tinggi node dalam tree, serta pointer `left` dan `right` yang menunjuk ke child kiri dan kanan. Selain itu, terdapat variabel `rotationCount` pada level tree untuk mencatat total rotasi yang terjadi selama operasi berlangsung.

```java
class Node {
    int key, height;
    Node left, right;
    Node(int d) {
        key = d;
        height = 1;
    }
}
```

#### Invariant Keseimbangan

AVL Tree menjaga keseimbangan dengan memastikan selisih tinggi antara subtree kiri dan kanan setiap node tidak melebihi 1. Selisih ini disebut **balance factor** dan dihitung dengan fungsi `getBalance()` yang mengembalikan `height(N.left) - height(N.right)`. Jika balance factor bernilai lebih dari 1 atau kurang dari -1, tree perlu diseimbangkan kembali melalui rotasi.

```java
int getBalance(Node N) {
    if (N == null) return 0;
    return height(N.left) - height(N.right);
}
```

#### Rotasi

Terdapat dua rotasi dasar yaitu `rightRotate` dan `leftRotate`. Keduanya bekerja dengan menggeser pointer antar node tanpa mengubah nilai datanya, lalu memperbarui tinggi node yang terlibat. Dari dua rotasi dasar ini terbentuk empat kasus penyeimbangan:

| Kasus | Kondisi | Penanganan |
|-------|---------|------------|
| Left-Left (LL) | `balance > 1` dan key masuk ke kiri-kiri | Right Rotate |
| Right-Right (RR) | `balance < -1` dan key masuk ke kanan-kanan | Left Rotate |
| Left-Right (LR) | `balance > 1` dan key masuk ke kiri-kanan | Left Rotate pada child, lalu Right Rotate |
| Right-Left (RL) | `balance < -1` dan key masuk ke kanan-kiri | Right Rotate pada child, lalu Left Rotate |

```java
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
```

#### Algoritma Insert

Insert dilakukan secara rekursif seperti BST biasa. Setelah node baru masuk, tinggi setiap node yang dilalui diperbarui, lalu balance factor dihitung. Jika tidak seimbang, salah satu dari empat kasus rotasi di atas diterapkan.

```java
Node insert(Node node, int key) {
    if (node == null) return (new Node(key));
    if (key < node.key)
        node.left = insert(node.left, key);
    else if (key > node.key)
        node.right = insert(node.right, key);
    else
        return node;

    node.height = 1 + max(height(node.left), height(node.right));
    int balance = getBalance(node);

    if (balance > 1 && key < node.left.key) return rightRotate(node);
    if (balance < -1 && key > node.right.key) return leftRotate(node);
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
```

#### Algoritma Delete

Jika node yang dihapus memiliki dua child, node tersebut tidak langsung dihapus melainkan digantikan nilainya dengan **in-order successor**, yaitu node terkecil dari subtree kanan melalui fungsi `minValueNode()`. Setelah itu, in-order successor dihapus dari posisi aslinya. Sama seperti insert, setiap node yang dilalui saat rekursi kembali ke atas akan dicek balance factor-nya dan dirotasi jika perlu.

```java
Node deleteNode(Node root, int key) {
    if (root == null) return root;
    if (key < root.key)
        root.left = deleteNode(root.left, key);
    else if (key > root.key)
        root.right = deleteNode(root.right, key);
    else {
        if ((root.left == null) || (root.right == null)) {
            Node temp = (root.left != null) ? root.left : root.right;
            if (temp == null) root = null;
            else root = temp;
        } else {
            Node temp = minValueNode(root.right);
            root.key = temp.key;
            root.right = deleteNode(root.right, temp.key);
        }
    }
    if (root == null) return root;
    root.height = max(height(root.left), height(root.right)) + 1;
    int balance = getBalance(root);
    if (balance > 1 && getBalance(root.left) >= 0) return rightRotate(root);
    if (balance > 1 && getBalance(root.left) < 0) {
        root.left = leftRotate(root.left);
        return rightRotate(root);
    }
    if (balance < -1 && getBalance(root.right) <= 0) return leftRotate(root);
    if (balance < -1 && getBalance(root.right) > 0) {
        root.right = rightRotate(root.right);
        return leftRotate(root);
    }
    return root;
}
```

#### Algoritma Search

Search bekerja seperti BST biasa — bandingkan key dengan node saat ini, lalu rekursi ke kiri jika lebih kecil atau ke kanan jika lebih besar. Kompleksitasnya terjamin O(log n) karena tree selalu dalam kondisi seimbang.

```java
boolean search(Node node, int key) {
    if (node == null) return false;
    if (key == node.key) return true;
    if (key < node.key) return search(node.left, key);
    return search(node.right, key);
}
```

---

### Weight-Balanced AVL Tree

#### Struktur Node

Perbedaan utama dari AVL Tree ada di field yang disimpan tiap node. Weight-Balanced Tree tidak menyimpan `height`, melainkan `size` yaitu jumlah seluruh node pada subtree tersebut termasuk dirinya sendiri. Setiap kali struktur tree berubah, `size` diperbarui melalui fungsi `updateSize()`.

```java
class WBTNode {
    int key, size;
    WBTNode left, right;
    WBTNode(int d) {
        key = d;
        size = 1;
    }
}

void updateSize(WBTNode n) {
    n.size = size(n.left) + size(n.right) + 1;
}
```

#### Parameter Keseimbangan

Implementasi ini menggunakan dua parameter dari paper Hirai & Yamamoto (2011), yaitu `DELTA = 3` dan `GAMMA = 2`. DELTA menentukan batas rasio berat antara dua subtree — sebuah node dianggap seimbang jika `δ × (size(a) + 1) >= (size(b) + 1)`. GAMMA menentukan jenis rotasi yang digunakan — jika `(size(a) + 1) < γ × (size(b) + 1)` maka cukup single rotation, jika tidak maka diperlukan double rotation.

```java
static final int DELTA = 3;
static final int GAMMA = 2;

boolean isBalanced(WBTNode a, WBTNode b) {
    return DELTA * (size(a) + 1) >= (size(b) + 1);
}

boolean isSingle(WBTNode a, WBTNode b) {
    return (size(a) + 1) < GAMMA * (size(b) + 1);
}
```

#### Mekanisme Balancing

Berbeda dengan AVL Tree yang mengecek keseimbangan setelah insert selesai, Weight-Balanced Tree menyeimbangkan tree secara inline melalui fungsi `balanceLeft` dan `balanceRight` yang dipanggil setiap kali rekursi kembali ke atas. `balanceLeft` dipanggil ketika operasi terjadi di sisi kiri sehingga sisi kanan berpotensi lebih berat, sedangkan `balanceRight` untuk kondisi sebaliknya. Kedua fungsi ini yang memilih antara single atau double rotation berdasarkan nilai `isSingle`.

```java
WBTNode balanceLeft(int key, WBTNode l, WBTNode r) {
    WBTNode node = new WBTNode(key);
    node.left = l;
    node.right = r;
    updateSize(node);
    if (!isBalanced(l, r)) {
        if (isSingle(r.left, r.right))
            return rotateLeft(node);
        else {
            node.right = rotateRight(r);
            updateSize(node);
            return rotateLeft(node);
        }
    }
    return node;
}

WBTNode balanceRight(int key, WBTNode l, WBTNode r) {
    WBTNode node = new WBTNode(key);
    node.left = l;
    node.right = r;
    updateSize(node);
    if (!isBalanced(r, l)) {
        if (isSingle(l.right, l.left))
            return rotateRight(node);
        else {
            node.left = rotateLeft(l);
            updateSize(node);
            return rotateRight(node);
        }
    }
    return node;
}
```

#### Algoritma Insert

Insert dilakukan rekursif seperti AVL Tree, namun setiap kali rekursi kembali ke atas node tidak langsung dikembalikan begitu saja — melainkan dibangun ulang melalui `balanceLeft` atau `balanceRight` yang sekaligus mengecek dan memperbaiki keseimbangan berdasarkan berat.

```java
WBTNode insert(WBTNode node, int key) {
    if (node == null) return new WBTNode(key);
    if (key < node.key)
        return balanceLeft(node.key, insert(node.left, key), node.right);
    else if (key > node.key)
        return balanceRight(node.key, node.left, insert(node.right, key));
    else
        return node;
}
```

#### Algoritma Delete

Saat menghapus node dengan dua child, Weight-Balanced Tree juga menggunakan in-order successor seperti AVL Tree. Bedanya, setelah successor ditemukan node dibangun ulang melalui `balanceRight` agar keseimbangan berdasarkan berat tetap terjaga.

```java
WBTNode deleteNode(WBTNode node, int key) {
    if (node == null) return null;
    if (key < node.key)
        return balanceLeft(node.key, deleteNode(node.left, key), node.right);
    else if (key > node.key)
        return balanceRight(node.key, node.left, deleteNode(node.right, key));
    else {
        if (node.left == null) return node.right;
        if (node.right == null) return node.left;
        WBTNode temp = minValueNode(node.right);
        return balanceRight(temp.key, node.left, deleteNode(node.right, temp.key));
    }
}
```

#### Algoritma Search

Search pada Weight-Balanced Tree identik dengan AVL Tree — rekursi ke kiri atau kanan berdasarkan perbandingan key. Kompleksitasnya tetap O(log n) karena tree selalu seimbang.

```java
boolean search(WBTNode node, int key) {
    if (node == null) return false;
    if (key == node.key) return true;
    if (key < node.key) return search(node.left, key);
    return search(node.right, key);
}
```

---

### Perbandingan Struktur dan Algoritma

| Aspek | AVL Tree | Weight-Balanced AVL Tree |
|-------|----------|--------------------------|
| Field per node | `key`, `height` | `key`, `size` |
| Kriteria seimbang | Selisih tinggi ≤ 1 | Rasio berat ≤ δ |
| Penentuan rotasi | Balance factor (tinggi) | `isBalanced` + `isSingle` (berat) |
| Waktu balancing | Setelah insert/delete selesai | Inline saat rekursi kembali |
| Jenis rotasi | LL, RR, LR, RL | Single atau double (ditentukan γ) |
| Parameter tambahan | Tidak ada | δ = 3, γ = 2 |

## Diagram dan Visualisasi

## Keunggulan 

## Kekurangan 

## Perbandingan antara AVL Tree Dasar dengan Weight-Balanced AVL Tree

## Analisis Kompleksitas Berdasarkan Struktur Tree

## Potensi Pengembangan 

## Hasil Implementasi

## Perbandingan Performa 

## Referensi
- http://dspace.univ-ouargla.dz/jspui/handle/123456789/28658
- https://doi.org/10.1017/S0956796811000104
