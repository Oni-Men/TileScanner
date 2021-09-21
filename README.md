# TileScanner - Scan tile entities inside the cuboid you specified.

## 使い方

### 1tick で処理するブロックの量を変更する

`/scantile option blocksPerTick [value]`

### ブロックをスキャンする

`/scantile scan [x1,y1,z1] [x2,y2,z2]`

x,y,z はカンマで区切ってください。
空間上の二点から出来る直方体の内部のブロックを全てスキャンします。

`/scantile scan [r]`

現在位置を中心とした(2r, 2r, 2r)の立方体をスキャンします。
r が大きすぎると不必要に範囲が増えるので、小さい範囲を手軽にスキャンするときに有効
