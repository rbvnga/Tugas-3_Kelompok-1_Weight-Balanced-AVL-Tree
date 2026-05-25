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

### 1. Rotasi AVL Tree

#### Left-Left (LL) — Right Rotate

Terjadi ketika node baru masuk ke subtree kiri-kiri sehingga balance factor > 1.

```
Sebelum:          Sesudah:
    z                 y
   / \               / \
  y   C             x   z
 / \        →      / \ / \
x   B             A  B B  C
```

#### Right-Right (RR) — Left Rotate

Terjadi ketika node baru masuk ke subtree kanan-kanan sehingga balance factor < -1.

```
Sebelum:          Sesudah:
  x                   y
 / \                 / \
A   y               x   z
   / \      →      / \ / \
  B   z            A  B B  C
```

#### Left-Right (LR) — Left Rotate lalu Right Rotate

Terjadi ketika node baru masuk ke subtree kiri-kanan.

```
Langkah 1 (Left Rotate pada y):

    z                 z
   / \               / \
  y   D             x   D
 / \       →       / \
A   x             y   C
   / \           / \
  B   C         A   B

Langkah 2 (Right Rotate pada z):

    z                 x
   / \               / \
  x   D             y   z
 / \       →       / \ / \
y   C             A  B C  D
```

#### Right-Left (RL) — Right Rotate lalu Left Rotate

Terjadi ketika node baru masuk ke subtree kanan-kiri.

```
Langkah 1 (Right Rotate pada y):

  z                 z
 / \               / \
A   y             A   x
   / \    →          / \
  x   D             B   y
 / \                   / \
B   C                 C   D

Langkah 2 (Left Rotate pada z):

  z                 x
 / \               / \
A   x             z   y
   / \    →      / \ / \
  B   y         A  B C  D
```

---

### 2. Mekanisme balanceLeft dan balanceRight

#### Single Rotation (isSingle = true)

Terjadi ketika subtree dalam (r.left) lebih kecil dari subtree luar (r.right).

```
Sebelum balanceLeft:        Sesudah rotateLeft:

   [key]                        [r]
   /    \                      /   \
 [l]    [r]       →         [key]  [r.R]
        /  \                /   \
      r.L  r.R            [l]  [r.L]
```

#### Double Rotation (isSingle = false)

Terjadi ketika subtree dalam (r.left) lebih besar dari subtree luar (r.right).

```
Langkah 1 (rotateRight pada r):    Langkah 2 (rotateLeft pada node):

   [key]                [key]                  [r.L]
   /    \               /    \                /      \
 [l]    [r]    →     [l]    [r.L]    →     [key]     [r]
        /  \                   \            /   \    /  \
      r.L  r.R                 [r]        [l] r.L.L r.L.R r.R
      /  \                    /  \
   r.L.L r.L.R            r.L.R  r.R
```

---

### 3. Contoh Insert pada AVL Tree

Insert berurutan: 10, 20, 30

```
Insert 10:        Insert 20:        Insert 30 (RR case):
   10                10                  10
                      \                    \          →    rotasi kiri
                      20                  20
                                            \
                                            30

Sesudah rotasi:
      20
     /  \
   10    30
```

Insert berurutan: 30, 20, 10

```
Insert 30:        Insert 20:        Insert 10 (LL case):
   30                30                  30
                    /                   /           →    rotasi kanan
                   20                  20
                                      /
                                     10

Sesudah rotasi:
      20
     /  \
   10    30
```

---

### 4. Contoh Insert pada Weight-Balanced AVL Tree

Insert berurutan: 1, 2, 3, 4, 5 (δ=3, γ=2)

```
Insert 1:    Insert 2:    Insert 3:       Insert 4:        Insert 5:
   1            1            1               2                 2
                 \            \             / \               / \
                  2            2           1   3             1   3
                                \                 \               \
                                 3                 4               4
                                                                     \
                                                                      5

Sesudah balanceLeft pada insert 5 (single rotation):
        2
       / \
      1   4
         / \
        3   5
```

---

### 5. Perbandingan Struktur Tree

Hasil insert 7 node berurutan (1, 2, 3, 4, 5, 6, 7):

**AVL Tree** — keseimbangan dijaga berdasarkan tinggi:
```
        4
       / \
      2   6
     / \ / \
    1  3 5  7
```

**Weight-Balanced AVL Tree** — keseimbangan dijaga berdasarkan jumlah node:
```
        4
       / \
      2   6
     / \ / \
    1  3 5  7
```

Pada dataset kecil yang terurut, kedua tree menghasilkan struktur yang sama. Perbedaan mulai terlihat pada dataset besar dengan distribusi acak, di mana Weight-Balanced Tree cenderung menghasilkan lebih sedikit rotasi karena keputusan rotasinya didasarkan pada jumlah node, bukan hanya tinggi.

## Aplikasi / Implementasi

Dalam penerapannya di dunia nyata, karakteristik mekanika dari kedua struktur data ini membuat masing-masing memiliki kecocokan kasus yang berbeda:

* **AVL Tree**
  * **Sistem yang Sering Cari Data (*Read-Heavy*):** Karena aturan seleksi tingginya sangat ketat, bentuk *tree* dijamin bakal selalu simetris dan optimal. Ini cocok banget buat sistem pencarian kamus digital, indeks *database* konvensional, atau aplikasi direktori yang datanya jarang berubah (jarang ada *insert*/*delete*) tapi sangat sering diakses.
  * **Sistem dengan Memori Terbatas:** Node pada AVL Tree cuma perlu menyimpan data integer kecil untuk `height`. Jadinya, struktur ini ramah buat perangkat *embedded system* yang memorinya terbatas.

* **Weight-Balanced AVL Tree (WBT)**
  * **Sistem yang Sering Modifikasi Data (*Write-Heavy*):** Sangat ideal dipakai pada sistem *log* berkas, *streaming data processing*, atau antrean transaksi keuangan yang frekuensi penambahan (*insert*) dan penghapusan (*delete*)-nya tinggi banget. Kelonggaran *balancing* pada WBT bikin proses tulis data gak gampang tersendat oleh rotasi.
  * **Pustaka Bahasa Pemrograman Fungsional:** Seperti yang dijelaskan oleh Hirai &amp; Yamamoto (2011), implementasi berbasis ukuran (*size*) ini dipakai secara luas di pustaka standar bahasa seperti Haskell (`Data.Set` dan `Data.Map`) karena parameter *size* memudahkan operasi gabungan (*union* / *intersection*) himpunan secara efisien.

## Keunggulan 

### 1. AVL Tree
* **Performa Search Lebih Konsisten pada Kasus Terburuk (*Worst-Case*):** Karena kriteria keseimbangan berbasis tinggi dikawal secara ketat ($|h(L) - h(R)| \leq 1$), tinggi maksimum *tree* ditekan seminimal mungkin. Hal ini menjamin operasi `search` selalu berada pada efisiensi puncak $O(\log n)$ secara stabil.
* **Alokasi Memori Node Lebih Ringan:** Hanya membutuhkan memori tambahan yang minimal untuk menyimpan satu field data primitif `height` pada setiap node.
* **Perilaku Struktur yang Sangat Matang:** Sebagai salah satu struktur pohon penyeimbang tertua, perilakunya sangat dapat diprediksi dengan dokumentasi dan studi kasus literatur yang sangat luas.

### 2. Weight-Balanced AVL Tree (WBT)
* **Efisiensi Overhead Rotasi yang Signifikan:** Melalui pengujian kelompok kami pada proses `insert` 1000 node, WBT terbukti memotong jumlah rotasi secara masif menjadi hanya **101 rotasi** dibandingkan AVL Tree yang memicu hingga **990 rotasi** (hampir 10 kali lebih efisien).
* **Throughput Operasi Modifikasi (*Write*) Lebih Cepat:** Minimnya jumlah penanganan rotasi berdampak langsung pada pemangkasan *runtime* eksekusi. Waktu `insert` pada WBT unggul di angka **2.79 ms**, jauh lebih responsif daripada AVL Tree yang memerlukan waktu **4.74 ms**.
* **Fitur Order Statistics secara Native:** Penyimpanan metadata `size` di setiap node memberikan keuntungan struktural, di mana operasi penentuan peringkat data (`rank`) dan pencarian elemen ke-$k$ (`select`) dapat dijalankan langsung dalam kompleksitas $O(\log n)$ tanpa perlu melakukan manipulasi struktur pohon (*augmentasi manual*) atau *traversal* penuh.


## Kekurangan 

### 1. AVL Tree
* **Biaya Penyeimbangan (*Rebalancing Cost*) yang Tinggi:** Aturan seleksi tinggi yang terlalu sensitif membuat struktur pohon seringkali terpaksa melakukan rotasi berantai di sepanjang jalur rekursi ke atas (*bottom-up*) setiap kali terjadi ketidakseimbangan kecil pasca operasi `insert` atau `delete`.
* **Runtime Operasi Penulisan Data Lebih Lambat:** Karena komputasi CPU tersita untuk melakukan kalkulasi *balance factor* dan eksekusi rotasi yang intens, waktu pengujian operasi `insert` membengkak hingga **4.74 ms**.
* **Operasi Rank dan Select Tidak Efisien:** Tanpa adanya modifikasi atau penambahan field secara eksplisit, operasi berbasis urutan statistik memerlukan *traversal* penuh yang memakan waktu sebanding dengan jumlah seluruh node ($O(n)$).

### 2. Weight-Balanced AVL Tree (WBT)
* **Struktur Pohon Cenderung Lebih Tinggi:** Akibat ambang batas toleransi keseimbangan rasio berat ($\delta = 3$) yang lebih longgar, bentuk *tree* bisa tumbuh lebih tinggi atau condong ke salah satu sisi. Hal ini terbukti dari data pengujian kami di mana tinggi akhir WBT menyentuh angka **17**, sedangkan AVL Tree berhasil menekan tinggi hingga **10**.
* **Potensi Degradasi Kecepatan Pencarian pada Skala Masif:** Walaupun pada sampel 1000 node waktu pencarian WBT terlihat impresif (**0.55 ms** vs **0.70 ms**), secara teoritis pada ukuran dataset yang sangat raksasa ($n \to \infty$), struktur pohon yang lebih tinggi akan memaksa lompatan penunjuk pointer (*pointer hopping*) lebih banyak sehingga berpotensi sedikit melambat dibanding AVL Tree.
* **Sangat Sensitif terhadap Konfigurasi Parameter:** Kredibilitas dan performa penyeimbangan WBT terkunci mati pada ketepatan penentuan nilai konstanta parameter $\delta$ dan $\gamma$. Deviasi kecil pada penentuan batas parameter ini dapat menyebabkan kriteria berat runtuh dan kehilangan sifat keseimbangannya.


## Perbandingan antara AVL Tree Dasar dengan Weight-Balanced AVL Tree

### 7.1 AVL Tree (Dasar)
 
AVL Tree adalah Binary Search Tree (BST) self-balancing yang menjaga **balance factor (BF)** di setiap node dengan syarat:
 
$$BF = |h(L) - h(R)| \leq 1$$
 
Keseimbangan ditegakkan melalui 4 tipe rotasi setiap kali insert atau delete melanggar properti ini. Setiap node menyimpan satu metadata berupa **height**. Tinggi terburuk ≈ 1.44 log₂ n.
 
**Mekanisme rotasi:**
- **LL Rotation** = node berat di kiri-kiri
- **RR Rotation** = node berat di kanan-kanan
- **LR Rotation** = node berat di kiri-kanan
- **RL Rotation** = node berat di kanan-kiri
**Struktur node AVL (Java):**
```java
class AVLNode {
    int key;
    int height;       // metadata: tinggi subtree
    AVLNode left, right;
}
```
 
---
 
### 7.2 Weight-Balanced AVL Tree (WB-AVL)
 
WB-AVL mengubah kriteria keseimbangan dari *height-based* menjadi **weight-based**. Keseimbangan dijaga dengan rasio berat subtree:
 
$$\frac{size(L)}{size(T)} \in [\alpha,\ 1 - \alpha], \quad \alpha \approx 0.29$$
 
di mana `size(T)` adalah jumlah total node di subtree T, dan `size(L)` adalah jumlah node di subtree kiri.
 
Setiap node menyimpan **size** (jumlah node di subtree) sebagai pengganti height.
 
**Struktur node WB-AVL (Java):**
```java
class WBNode {
    int key;
    int size;         // metadata: jumlah node di subtree ini
    WBNode left, right;
}
```
 
---
 
### 7.3 Perbandingan Mendasar: AVL vs WB-AVL
 
| Aspek | AVL Tree | WB-AVL Tree |
|---|---|---|
| **Kriteria keseimbangan** | `\|h(L) − h(R)\| ≤ 1` (height-based) | `size(L)/size(T) ∈ [α, 1−α]` (weight-based) |
| **Metadata per node** | `height` (1 integer) | `size` (1 integer) |
| **Tinggi terburuk** | ≈ 1.44 log₂ n | ≈ 1.29 log₂ n |
| **Mekanisme rebalance** | Rotasi LL, RR, LR, RL | Single/double rotasi + update size |
| **Frekuensi rotasi** | Tinggi (BF ±1 ketat) | Sedang (rasio lebih longgar) |
| **Rank / Select** | O(n) tanpa augmentasi | **O(log n) native** |
| **Order statistics** | Perlu augmentasi eksplisit | Sudah built-in dari size |
| **Space per node** | O(1) overhead | O(1) overhead |
| **Space total** | O(n) | O(n) |
| **Kompleksitas implementasi** | Sedang | Sedang–Tinggi |
| **Use case ideal** | General BST, read-heavy | Ranking engine, order statistics |
 
---
 
### 7.4 Perbedaan Kriteria Keseimbangan
 
**AVL Tree** menggunakan selisih tinggi , pohon dianggap tidak seimbang jika satu sisi lebih tinggi satu level dari sisi lain. Ini sangat ketat, sehingga rotasi sering terpicu bahkan untuk ketidakseimbangan kecil.
 
**WB-AVL** menggunakan proporsi berat , pohon dianggap tidak seimbang jika subtree kiri menyimpan terlalu sedikit atau terlalu banyak node dibanding total. Threshold α ≈ 0.29 memberi toleransi lebih besar, sehingga rotasi lebih jarang terpicu.
 
Contoh kasus di mana keduanya berbeda:
```
Pohon berikut VALID di WB-AVL tapi TIDAK VALID di AVL:
 
        10
       /  \
      5    15
     / \
    3   7
   /
  1
 
AVL  : BF(10) = h(kiri)=3, h(kanan)=1 → |3-1|=2 > 1 → PERLU ROTASI
WB-AVL: size(kiri)=4, size(T)=6 → 4/6=0.67 → masih dalam [0.29, 0.71] → VALID
```
 
---
 
### 7.5 Perbedaan Tinggi Pohon
 
AVL Tree menjamin tinggi maksimum berdasarkan sifat Fibonacci tree:
 
$$h_{AVL} \leq 1.44 \log_2(n + 2)$$
 
WB-AVL menjamin tinggi lebih pendek karena distribusi node lebih merata secara berat:
 
$$h_{WB} \leq \log_{1/\alpha}(n) \approx 1.29 \log_2 n \quad (\alpha \approx 0.29)$$
 
Pohon yang lebih pendek berarti operasi search rata-rata memerlukan lebih sedikit perbandingan.
 
---
 
### 7.6 Keunggulan Kritis WB-AVL: Rank dan Select Native
 
Karena setiap node menyimpan `size`, operasi **rank** (cari posisi ke-k dari terkecil) dan **select** (cari elemen ke-k) dapat dilakukan dalam O(log n) tanpa augmentasi tambahan:
 
```java
// Rank: posisi key dalam urutan terurut
int rank(WBNode node, int key) {
    if (node == null) return 0;
    if (key == node.key) return size(node.left) + 1;
    if (key < node.key)  return rank(node.left, key);
    else                 return size(node.left) + 1 + rank(node.right, key);
}
 
// Select: elemen ke-k terkecil
WBNode select(WBNode node, int k) {
    int leftSize = size(node.left) + 1;
    if (k == leftSize) return node;
    if (k < leftSize)  return select(node.left, k);
    else               return select(node.right, k - leftSize);
}
// Keduanya T(n) = T(n/2) + O(1) → O(log n)
```
 
Pada AVL Tree biasa, operasi yang sama memerlukan traversal seluruh pohon O(n) , kecuali ditambahkan field `size` secara eksplisit (augmentasi manual).
 
---
 
### 7.7 Kesimpulan Perbandingan Teori
 
WB-AVL bukan sekadar "AVL yang dimodifikasi sedikit", ia mengubah **filosofi keseimbangan** dari tinggi ke berat. Ini menghasilkan pohon yang secara statistik lebih merata distribusinya, lebih pendek, dan secara native mendukung operasi order-statistics. Trade-off-nya adalah implementasi yang sedikit lebih kompleks dan pohon yang secara tinggi bisa sedikit lebih tidak merata dibanding AVL klasik.
 
---

## Analisis Kompleksitas Berdasarkan Struktur Tree

### 8.1 Kompleksitas Waktu AVL Tree
 
Jaminan tinggi pohon ≤ 1.44 log₂ n berasal dari sifat Fibonacci tree sebagai kasus terburuk AVL. Semua operasi utama berjalan dalam O(log n):
 
| Operasi | Best Case | Average Case | Worst Case |
|---|:---:|:---:|:---:|
| Search | O(1) | O(log n) | O(log n) |
| Insert | O(log n) | O(log n) | O(log n) |
| Delete | O(log n) | O(log n) | O(log n) |
| Rank / Select | O(1) | O(n) | O(n) |
| Rotasi per insert | O(1) | O(1) amortized | O(log n) |
| Space | — | O(n) | O(n) |
 
**Analisis rotasi insert:**
Setiap insert memerlukan paling banyak **2 rotasi** (untuk double rotation LR/RL). Namun traversal bottom-up untuk update BF sepanjang jalur ke root tetap O(log n). Dalam n operasi insert berurutan, rata-rata terjadi ~1 rotasi per insert.
 
**Analisis rotasi delete:**
Delete lebih mahal dari insert pada AVL , rotasi bisa terjadi di setiap level jalur ke root (O(log n) rotasi dalam satu operasi delete, bukan O(1)).
 
---
 
### 8.2 Kompleksitas Waktu WB-AVL Tree
 
Tinggi pohon dijamin oleh weight ratio α:
 
$$h_{WB} \leq \log_{1/\alpha}(n) \approx 1.29 \log_2 n \quad (\alpha \approx 0.29)$$
 
| Operasi | Best Case | Average Case | Worst Case |
|---|:---:|:---:|:---:|
| Search | O(1) | O(log n) | O(log n) |
| Insert | O(log n) | O(log n) | O(log n) |
| Delete | O(log n) | O(log n) | O(log n) |
| **Rank** | O(1) | **O(log n)** | **O(log n)** |
| **Select** | O(1) | **O(log n)** | **O(log n)** |
| Update `size` saat insert/delete | O(log n) | O(log n) | O(log n) |
| Space | — | O(n) | O(n) |
 
**Analisis update size:**
Setiap insert/delete meng-update `size` di semua ancestor node sepanjang jalur dari leaf ke root → O(log n). Ini tidak menambah kompleksitas asimptotik karena traversal tersebut memang sudah dilakukan untuk rebalancing.
 
**Analisis frekuensi rotasi dibanding AVL:**
```
Untuk n operasi insert berurutan:
 
AVL Tree : ~1.0 rotasi/insert  → n rotasi total
WB-AVL   : ~0.5 rotasi/insert  → n/2 rotasi total
 
WB-AVL melakukan rotasi ≈ 2× lebih jarang secara amortized,
karena threshold berbasis rasio lebih toleran terhadap
distribusi asimetris kecil.
```
 
---
 
### 8.3 Perbandingan Kompleksitas AVL vs WB-AVL
 
| Operasi | AVL Tree | WB-AVL Tree | Keterangan |
|---|:---:|:---:|---|
| Search | O(log n) | O(log n) | WB-AVL konstan lebih kecil (pohon lebih pendek) |
| Insert | O(log n) | O(log n) | WB-AVL rotasi lebih jarang |
| Delete | O(log n) | O(log n) | Setara, WB-AVL update size O(log n) |
| Rank | **O(n)** | **O(log n)** | Keunggulan utama WB-AVL |
| Select | **O(n)** | **O(log n)** | Keunggulan utama WB-AVL |
| Space | O(n) | O(n) | Setara (size menggantikan height) |
| Tinggi pohon | 1.44 log₂ n | 1.29 log₂ n | WB-AVL ~10% lebih pendek |
| Rotasi/insert | ~1.0× | ~0.5× | WB-AVL 2× lebih efisien |
 
---
 
### 8.4 Analisis Kompleksitas Rotasi (Amortized)
 
Dalam sequence n operasi campuran insert dan delete:
 
**AVL Tree:**
- Insert: amortized O(1) rotasi, total traversal O(log n)
- Delete: bisa O(log n) rotasi dalam satu operasi (berbeda dari insert)
- Total rotasi untuk n operasi: O(n log n) pada workload delete-heavy
**WB-AVL Tree:**
- Insert: amortized O(1) rotasi, lebih jarang dari AVL
- Delete: amortized O(log n), mirip insert karena threshold berbasis rasio
- Total rotasi untuk n operasi: O(n) pada workload seimbang
---
 
### 8.5 Kompleksitas Ruang (Space Complexity)
 
Kedua tree memiliki space complexity O(n). Perbedaannya hanya pada field yang disimpan per node:
 
| | AVL Node | WB-AVL Node |
|---|---|---|
| `key` | yes | yes |
| `left`, `right` | yes | yes |
| `height` | yes | no (diganti size) |
| `size` | no | yes |
| **Total per node** | **4 field** | **4 field** |
 
Tidak ada overhead memori tambahan , WB-AVL hanya mengganti `height` dengan `size`, bukan menambahkan field baru.
 
---

## Potensi Pengembangan 

### 9.1 Pengembangan AVL Tree
 
#### 9.1.1 Persistent AVL Tree
AVL Tree dapat dikembangkan menjadi **persistent data structure** menggunakan teknik *path copying*: setiap modifikasi menghasilkan versi baru pohon tanpa menghapus versi lama.
 
```
v1 (root A) → insert(X) → v2 (root A')
                         → v1 tetap bisa diakses
```
 
Manfaat:
- Database dengan **time-travel query** ("tampilkan data per tanggal X")
- Sistem **undo/redo** pada editor teks atau software desain
- Functional programming yang menghindari mutation
Kompleksitas: O(log n) per operasi, O(log n) space tambahan per versi.
 
#### 9.1.2 Concurrent / Lock-free AVL Tree
AVL Tree standar tidak aman untuk multi-thread karena rotasi mengubah banyak pointer sekaligus. Pengembangan ke depan:
- **Fine-grained locking**: lock hanya node yang sedang dimodifikasi, bukan seluruh tree
- **Lock-free AVL**: menggunakan operasi CAS (Compare-And-Swap) atomic
- Relevan untuk **in-memory database multi-core** dan sistem real-time
---
 
### 9.2 Pengembangan WB-AVL Tree
 
#### 9.2.1 Persistent WB-AVL Tree
Karena WB-AVL sudah menyimpan `size` di setiap node, persistent version-nya memiliki keunggulan tambahan: setiap snapshot versi juga mendukung **rank/select O(log n)** secara langsung. Ini digunakan pada **persistent order-statistics tree** untuk analisis data historis , misalnya "siapa yang berada di peringkat ke-100 pada tanggal X?"
 
#### 9.2.2 Adaptive Alpha (α Dinamis)
Nilai α pada WB-AVL saat ini bersifat statis (≈ 0.29). Pengembangan ke depan: **adaptive α** yang berubah dinamis berdasarkan pola workload:
- Jika workload sedang **write-heavy** → naikkan α sedikit (kurangi frekuensi rotasi)
- Jika workload bergeser ke **read-heavy** → turunkan α (perbaiki balance, pohon lebih pendek)
Hasilnya adalah WB-AVL yang **self-tuning** tanpa konfigurasi manual.
 
---
 
### 9.3 Pengembangan Gabungan (AVL + WB-AVL)
 
#### 9.3.1 Tiered Index Structure
Menggabungkan AVL Tree dan WB-AVL dalam arsitektur bertingkat:
```
Layer 1 (hot data)  : AVL Tree di L1/L2 cache — prioritas kecepatan search
Layer 2 (warm data) : WB-AVL di DRAM — prioritas rank/select dan throughput
Layer 3 (cold data) : M-way variant di disk — prioritas kapasitas
```
Data panas (sering diakses) naik ke layer lebih atas secara otomatis → arsitektur ini digunakan pada **database buffer pool** modern.
 
#### 9.3.2 Cache-Oblivious Layout
Menyusun ulang node di memori menggunakan layout **van Emde Boas** sehingga traversal pohon menghasilkan cache miss minimal tanpa perlu mengetahui cache size secara eksplisit. Karena WB-AVL pohonnya lebih pendek, manfaat cache-oblivious layout lebih terasa dibanding AVL biasa.
 
---

## Hasil Implementasi

**AVL Tree** 
<br>
<img width="254" height="339" alt="Screenshot 2026-05-25 122344" src="https://github.com/user-attachments/assets/cac44ba3-3a13-4f92-8287-321f1b22c5e8" /> <br>

**Weight Balanced AVL Tree**
<br>
<img width="394" height="317" alt="Screenshot 2026-05-25 122418" src="https://github.com/user-attachments/assets/d1526513-100a-4baa-a353-36a79709426e" /> <br>


## Perbandingan Performa 
**Insert 1000 nodes** 
| | AVL | WBT|
| :--- | :---: | :---: |
| Waktu | 4.74 ms | 2.79 ms |
| Rotasi | 990 | 101 |
| Tinggi | 10 | 17 |

**Search 1000 nodes** 
| | AVL | WBT|
| :--- | :---: | :---: |
| Waktu | 0.70 ms | 0.55 ms |

**Delete 500 nodes** 
| | AVL | WBT |
| :--- | :---: | :---: |
| Waktu | 0.99 ms | 0.93 ms |
| Rotasi | 248 | 143 |
| Tinggi | 10 | 12 |
| Size | 500 | 452 |

- **Rotasi WBT jauh lebih sedikit** — insert hanya 101 rotasi vs 990 pada AVL (sekitar 10x lebih sedikit), karena threshold keseimbangan WBT lebih longgar
- **Tinggi WBT sedikit lebih besar** — 17 vs 10 saat insert, konsekuensi wajar dari keseimbangan yang lebih longgar
- **Search dan delete performa setara** — selisih waktu minimal, karena tinggi akhir tree tidak terlalu berbeda
- Hasil ini konsisten dengan temuan pada paper Hirai & Yamamoto (2011) yang menyatakan WBT dengan Δ=3, Γ=2 memiliki performa kompetitif dibanding AVL

## Referensi
- Hirai, Y., & Yamamoto, K. (2011). Balancing weight-balanced trees. *Journal of Functional Programming*, 21(3), 287–307. https://doi.org/10.1017/S0956796811000104
- Gomes, A. (n.d.). *AVL Tree*. Universitas Ouargla. http://dspace.univ-ouargla.dz/jspui/handle/123456789/28658
