package graph

import exceptions.*

import java.io.File

class UnweightedGraph(
    private val adjacency: HashMap<Vertex, HashMap<Vertex, Any?>> = HashMap(),
    private val type: GraphType = GraphType.UNDIRECTED
) : Graph {
    constructor(type: GraphType, adjacency: HashMap<Vertex, HashMap<Vertex, Any?>> = HashMap()) : this(adjacency, type)

    constructor(graph: UnweightedGraph) : this(HashMap(graph.adjacency), graph.type)

    override fun addVertex(vertex: Vertex): UnweightedGraph {
        if (adjacency.containsKey(vertex)) {
            throw VertexAlreadyExistException("Cannot add vertex \"$vertex\", already exist in graph.")
        } else {
            val result = HashMap(adjacency)
            result[vertex] = HashMap()
            return UnweightedGraph(result, type)
        }
    }

    override fun removeVertex(vertex: Vertex): UnweightedGraph {
        if (!adjacency.containsKey(vertex)) {
            throw VertexNotFoundException("graph.Vertex $vertex not found in graph.")
        }
        val result = HashMap(adjacency)
        result.remove(vertex)
        if (type == GraphType.UNDIRECTED) {
            result.values.forEach { edges ->
                edges.remove(vertex)
            }
        }
        return UnweightedGraph(result, type)
    }

    fun addEdge(source: Vertex, destination: Vertex): UnweightedGraph {
        val result = HashMap(adjacency)

        val sourceNotFound = !result.containsKey(source)
        val destinationNotFound = !result.containsKey(destination)

        if (sourceNotFound && destinationNotFound) {
            throw VertexNotFoundException("Vertices \"$source\" and \"$destination\" are not found in graph.")
        } else if (sourceNotFound) {
            throw VertexNotFoundException("graph.Vertex \"$source\" is not found in graph.")
        } else if (destinationNotFound) {
            throw VertexNotFoundException("graph.Vertex \"$destination\" is not found in graph.")
        }

        if (result[source]!!.containsKey(destination)) {
            throw EdgeAlreadyExistException("Edge ($source, $destination) already exist!")
        }

        result[source]!![destination] = null
        if (type == GraphType.UNDIRECTED) {
            result[destination]!![source] = null
        }

        return UnweightedGraph(result, type)
    }

    override fun removeEdge(source: Vertex, destination: Vertex): UnweightedGraph {
        val result = HashMap(adjacency)

        val sourceNotFound = !result.containsKey(source)
        val destinationNotFound = !result.containsKey(destination)

        if (sourceNotFound && destinationNotFound) {
            throw VertexNotFoundException("Vertices \"$source\" and \"$destination\" are not found in graph.")
        } else if (sourceNotFound) {
            throw VertexNotFoundException("graph.Vertex \"$source\" is not found in graph.")
        } else if (destinationNotFound) {
            throw VertexNotFoundException("graph.Vertex \"$destination\" is not found in graph.")
        } else if (source == destination) {
            throw InvalidInputException("Vertices \"$source\" and \"$destination\" are identical. ")
        }

        if (!result[source]!!.keys.contains(destination)) {
            throw EdgeNotFoundException("Edge ($source, $destination) not found.")
        }

        result[source]!!.keys.removeIf { it == destination }
        if (type == GraphType.UNDIRECTED) {
            if (!result[destination]!!.keys.contains(source)) {
                throw EdgeNotFoundException("Edge ($destination, $source) not found.")
            }
            result[destination]!!.keys.removeIf { it == source }
        }

        return UnweightedGraph(result, type)
    }

    override fun readFromFile(filename: String): UnweightedGraph {
        var graphType: GraphType = GraphType.UNDIRECTED
        val adjacency: HashMap<Vertex, HashMap<Vertex, Any?>> = HashMap()

        var input = File(filename).readLines()

        val inputType = input[0].split(", ")

        if (inputType[0] != "UNWEIGHTED") {
            throw InvalidFileException("Graph type is weighted, but expected unweighted.")
        }

        if (inputType[1] == "DIRECTED") {
            graphType = GraphType.DIRECTED
        }

        input = input.drop(1)

        input.forEach { adjacencyList ->
            val sourceVertex = Vertex(adjacencyList.substringBefore(':'))
            val adjacentVertices: HashMap<Vertex, Any?> = HashMap()
            if (adjacencyList.substringAfter('[').substringBefore(']') != "") {
                adjacencyList.substringAfter('[').substringBefore(']').split(", ").forEach {
                    val destinationVertex = Vertex(it)
                    adjacentVertices[destinationVertex] = null
                }
                if (!adjacency.containsKey(sourceVertex)) {
                    adjacency[sourceVertex] = HashMap()
                }
                adjacentVertices.forEach { edge ->
                    val destinationVertex = edge.key

                    if (!adjacency.containsKey(destinationVertex)) {
                        adjacency[destinationVertex] = HashMap()
                    }
                    adjacency[sourceVertex]!![destinationVertex] = edge.value
                    if (graphType == GraphType.UNDIRECTED) {
                        adjacency[destinationVertex]!![sourceVertex] = edge.value
                    }
                }
            }
        }
        return UnweightedGraph(adjacency, type)
    }

    override fun printToFile(filename: String) {
        var output = "UNWEIGHTED, $type\n"
        adjacency.forEach { vertex ->
            var counter = 0
            output += vertex.key.toString() + ": ["
            vertex.value.forEach { adjacentVertex ->
                output += if (counter == vertex.value.size - 1) {
                    "${adjacentVertex.key}"
                } else {
                    "${adjacentVertex.key}, "
                }
                ++counter
            }
            output += "]\n"
        }
        val newFile = File(filename)
        newFile.writeText(output)
    }

    override fun getDegrees(): HashMap<Vertex, Int> {
        val degrees = HashMap<Vertex, Int>()
        adjacency.forEach {
            degrees[it.key] = it.value.size
        }
        return degrees
    }

    override fun getHingeVertices(): List<Vertex> {
        if (type == GraphType.DIRECTED) {
            val result = mutableListOf<Vertex>()
            adjacency.forEach {
                if (it.value.containsKey(it.key)) {
                    result.add(it.key)
                }
            }
            return result
        }
        return listOf()
    }

    companion object {
        fun createRandomGraph(verticesCount: Int, type: GraphType): UnweightedGraph {
            val cities = File("cities.txt").readLines()

            val vertices = ArrayList<Vertex>()
            for (i in 0 until verticesCount) {
                val vertex = Vertex(cities[(1 until cities.size).random()])
                vertices.add(vertex)
            }

            val adjacency = HashMap<Vertex, HashMap<Vertex, Any?>>()

            vertices.forEach {
                adjacency[it] = HashMap()
            }

            for (source in vertices) {
                for (destination in vertices) {
                    adjacency[source]?.put(destination, null)
                    adjacency[destination]?.put(source, null)
                }
            }
            return UnweightedGraph(adjacency, type)
        }
    }
}
    