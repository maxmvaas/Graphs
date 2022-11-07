package graph

import exceptions.*

import java.io.File
import java.io.IOException

import kotlin.random.Random

class WeightedGraph(
    private val adjacency: HashMap<Vertex, HashMap<Vertex, Int>> = HashMap(),
    private val type: GraphType = GraphType.UNDIRECTED
) : Graph {
    constructor(type: GraphType, adjacency: HashMap<Vertex, HashMap<Vertex, Int>> = HashMap()) : this(adjacency, type)

    constructor(graph: WeightedGraph) : this(HashMap(graph.adjacency), graph.type)

    override fun addVertex(vertex: Vertex): WeightedGraph {
        if (vertex.toString().isEmpty()) {
            throw IOException("graph.Vertex must have a name.")
        }
        if (adjacency.containsKey(vertex)) {
            throw VertexAlreadyExistException("Cannot add vertex \"$vertex\", already exist in graph.")
        } else {
            val result = HashMap(adjacency)
            result[vertex] = HashMap()
            return WeightedGraph(result, type)
        }
    }

    override fun removeVertex(vertex: Vertex): WeightedGraph {
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
        return WeightedGraph(result, type)
    }

    fun addEdge(source: Vertex, destination: Vertex, weight: Int): WeightedGraph {
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

        result[source]!![destination] = weight
        if (type == GraphType.UNDIRECTED) {
            result[destination]!![source] = weight
        }

        return WeightedGraph(result, type)
    }

    override fun removeEdge(source: Vertex, destination: Vertex): WeightedGraph {
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

        return WeightedGraph(result, type)
    }

    override fun readFromFile(filename: String): WeightedGraph {
        var graphType: GraphType = GraphType.UNDIRECTED
        val adjacency: HashMap<Vertex, HashMap<Vertex, Int>> = HashMap()

        var input = File(filename).readLines()

        val inputType = input[0].split(", ")

        if (inputType[0] != "WEIGHTED") {
            throw InvalidFileException("Graph type is unweighted, but expected weighted.")
        }

        if (inputType[1] == "DIRECTED") {
            graphType = GraphType.DIRECTED
        }

        input = input.drop(1)

        input.forEach { adjacencyList ->
            val sourceVertex = Vertex(adjacencyList.substringBefore(':'))
            val adjacentVertices: HashMap<Vertex, Int> = HashMap()
            if (adjacencyList.substringAfter('[').substringBefore(']') != "") {
                adjacencyList.substringAfter('[').substringBefore(']').split(", ").forEach {
                    val weight = Integer.parseInt(it.substringAfter('(').substringBefore(')'))
                    val destinationVertex = Vertex(it.substringBefore('('))
                    adjacentVertices[destinationVertex] = weight
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
        return WeightedGraph(adjacency, type)
    }

    override fun printToFile(filename: String) {
        var output = "WEIGHTED, $type\n"
        adjacency.forEach { vertex ->
            var counter = 0
            output += vertex.key.toString() + ": ["
            vertex.value.forEach { adjacentVertex ->
                output += if (counter == vertex.value.size - 1) {
                    "${adjacentVertex.key}(${adjacentVertex.value})"
                } else {
                    "${adjacentVertex.key}(${adjacentVertex.value}), "
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
        fun createRandomGraph(verticesCount: Int, minWeight: Int, maxWeight: Int, type: GraphType): WeightedGraph {
            val cities = File("cities.txt").readLines()

            val vertices = ArrayList<Vertex>()
            for (i in 0 until verticesCount) {
                val vertex = Vertex(cities[(1 until cities.size).random()])
                vertices.add(vertex)
            }

            val adjacency = HashMap<Vertex, HashMap<Vertex, Int>>()

            vertices.forEach {
                adjacency[it] = HashMap()
            }

            for (source in vertices) {
                for (destination in vertices) {
                    val weight = (minWeight..maxWeight).random()
                    adjacency[source]?.put(destination, weight)
                    if (type == GraphType.UNDIRECTED) {
                        adjacency[destination]?.put(source, adjacency[source]?.get(destination)!!)
                    } else {
                        adjacency[destination]?.put(source, Random.nextInt(minWeight, maxWeight))
                    }
                }
            }
            return WeightedGraph(adjacency, type)
        }
    }
}
