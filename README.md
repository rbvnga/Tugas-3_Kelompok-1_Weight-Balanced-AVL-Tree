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
