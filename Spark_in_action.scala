# Ebook Spark in Action

# 9. Connecting the dots with GraphX

## 9.1. Graph processing with Spark

### Graph là gì

### Tại sao lại sử dụng Graph, các bài toán ứng dụng liên quan

### Sử dụng GraphX API

### 9.1.1. Khởi tạo Constructing graphs using GraphX API

- Graph là class chính trong ClassX, gồm các đỉnh và cạnh và các opetator để transforming GraphX
- Vertices và Edges trong spark được xây dựng từ RĐ:
    - VertexRDDs: chứ tuple gồm 2 phần tử là 1 vertex ID kiểu Long and a property object kiểu tùy ý.
    - EdgeRDDs chứa Edge objects, bao gồm source and destination vertex
    IDs (srcId and dstId, respectively) and a property object kiểu tùy ý(attr field).

**CONSTRUCTING THE GRAPH**

- Có nhiều cách để khởi tạo Graph, ta có thể sử dụng 1 RDD chứa tuple(vertex ID, a vertex property object) và 1 RDD chứa các đối tượng Edge.

### 9.1.2 Chuyển đổi Transforming graphs

- Map() các Vertices và Edges trong Graph
- Aggregating
- Join and Filters
- Trong spark, ngoài Graph còn class GraphOps. So sánh sự khác nhau ntn?
- Sau đây, ta trình bày về các toán tử quan trọng và hay được sử dụng của Graph

### **MAPPING EDGES AND VERTICES**

- Ta có thể thực hiện chuyển đổi edge và vertex property objects với  mapEdges và mapVertices methods.

```scala
case class Relationship(relation: String)
val newgraph = graph.mapEdges((partid, iter) =>
	iter.map(edge => Relation(edge.attr)))
newgraph.edges.collect() // thuoc tinh cua edge da thanh object

```

Trong code trên, ta tạo 1 graph mới bằng cách thay đổi thuộc tính của các Edges từ dạng String sang dạng đối tượng RelationShip

- Ta còn có thể map Edges bằng mapTriplets. Với mỗi cạnh, nó trả về cho ta: srcId, dstId, and attr fields của Edge và thuộc tính của các đỉnh srcAttr and dstAttr.
- Với Vertices, ta thực hiện mapVertices với trường hợp: chuyển đổi các đối tượng thuộc tính Vertices từ dạng Person sang dạng PersonExt

```scala
// tao class moi
case class PersonExt(name:String, age:Int, children:Int=0, friends:Int=0,
 married:Boolean=false)
val newGraphExt = newgraph.mapVertices((id, person) 
 => PersonExt(person.name, person.age)
```

### **AGGREGATING MESSAGES**

- Được sử dụng để chạy 1 hàm trên mỗi đỉnh của đồ thị và tùy chọn gửi tin nhắn đến các đỉnh lân cận của nó.
- Nghĩa là ta dùng để kết nối các đỉnh có quan hệ với nhau và lấy thông tin từ chúng với thuộc tính của cạnh và thuộc tính của đỉnh.
- Chúng ta cần truyền cho nó 3 tham số:  sendMsg and mergeMsg, and the tripletFields property.
- EdgeContext: chứa IDs and property objects for source and destination vertices, the property object for the edge, 2 method sendToSrc and sendToDst.

### JOINING GRAPH DATA

- Dùng để ta kết nối graph ban đầu và các

vertex messages vừa tạo với **AGGREGATING MESSAGES**

- Tham số truyền vào:
1 RDD kiểu tuple chứ vertex IDs và new vertex objects
1 mapping function chuyển đổi old vertex property objects đến new vertex objects

**GraphX’s Pregel implementation**

```scala

def apply[VD: ClassTag, ED: ClassTag, A: ClassTag]
 (graph: Graph[VD, ED],
 initialMsg: A,
 maxIterations: Int = Int.MaxValue,
 activeDirection: EdgeDirection = EdgeDirection.Either)
 (vprog: (VertexId, VD, A) => VD,
 sendMsg: EdgeTriplet[VD, ED] => Iterator[(VertexId, A)],
 mergeMsg: (A, A) => A)
 : Graph[VD, ED]
```

### SELECTING GRAPH SUBSETS

- subgraph—Selects vertices and edges với điều kiện cho trước

Gồm điều kiện của cạnh, điều kiện của đỉnh

```scala
def subgraph(
 epred: EdgeTriplet[VD, ED] => Boolean = (x => true),
 vpred: (VertexId, VD) => Boolean = ((v, d) => true))
 : Graph[VD, ED]
```

- mask—Selects only the vertices ở trong graph khác
- filter— kết hợp 2 phần trên

## 9.2. Graph algorithms

- Với grahx, các điểm dữ liệu được tổ chức thành đồ thị. Từ đó ta có thể sử dụng các thuật toán được xây dựng sẵn để có thể giải quyết dễ dàng các vấn đề thực tế
- Shortest paths: Tìm đường đi ngắn nhất dến 1 tập các đỉnh
- 

### 9.2.1 Presentation of the dataset

```scala
// import thu vien
import org.apache.spark.graphx._
// tao class person, la thuoc tinh dang doi tuong cua Vertices
case class Person(name:String, age:Int)
// Tao 2 RDD
val vertices = sc.parallelize(Array((1L, Person("Homer", 39)),
 (2L, Person("Marge", 39)), (3L, Person("Bart", 12)),
 (4L, Person("Milhouse", 12))))
val edges = sc.parallelize(Array(Edge(4L, 3L, "friend"),
 Edge(3L, 1L, "father"), Edge(3L, 2L, "mother"),
 Edge(1L, 2L, "marriedTo")))

val graph = Graph(vertices, edges)
graph.vertices.count()
graph.edges.count(
```
