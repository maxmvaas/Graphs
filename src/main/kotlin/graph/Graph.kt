package graph

interface Graph {
    fun printToFile(filename: String)

    fun addVertex(vertex: Vertex): Graph

    fun removeVertex(vertex: Vertex): Graph

    fun removeEdge(source: Vertex, destination: Vertex): Graph

    fun readFromFile(filename: String): Graph

    fun getDegrees(): HashMap<Vertex, Int>

    fun getHingeVertices(): List<Vertex>
}