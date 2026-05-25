# Tugas-3_Kelompok-1_Weight-Balanced-AVL-Tree
| Nama Anggota | NRP |
| :--- | :---|
| Putri Permata Sabila | 5027251047 |
| Revalinda Bunga Nayla Laksono | 5027251011 |
| Nathania Tiara Wahyudi | 5027251089 |
| Jude Athala Yazid Sari| 5027251098 |
| Muhammad Ridwan | 5027251113 |


## Problem Statement

## Struktur Tree dan Algoritma

## Diagram dan Visualisasi

## Keunggulan 

## Kekurangan 

## Perbandingan antara AVL Tree Dasar dengan Weight-Balanced AVL Tree

## Analisis Kompleksitas Berdasarkan Struktur Tree

## Potensi Pengembangan 

## Hasil Implementasi

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