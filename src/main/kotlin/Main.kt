import graph.*

import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.NumberFormatException

private lateinit var graph: Graph

private const val invalidInputMessage = "Invalid input, try again.\n"

fun main() {
    init()
}

tailrec fun init() {
    print(
        "1. Create graph.\n" +
                "2. Load graph from file.\n" +
                "3. Create random graph.\n" +
                "4. Exit.\n>>> "
    )
    when (readln()) {
        "1" -> createGraph()
        "2" -> loadGraphFromFile()
        "3" -> createRandomGraph()
        "4" -> return
        else -> {
            print(invalidInputMessage)
            init()
        }
    }
}

fun createGraph() {
    println("Please select the graph type: ")
    print("Weighted: \n\t1. Directed.\n\t2. Undirected.\nUnweighted: \n\t3.Directed.\n\t4.Undirected.\n>>> ")
    when (readln()) {
        "1" -> graph = WeightedGraph(GraphType.DIRECTED)
        "2" -> graph = WeightedGraph(GraphType.UNDIRECTED)
        "3" -> graph = UnweightedGraph(GraphType.DIRECTED)
        "4" -> graph = UnweightedGraph(GraphType.UNDIRECTED)
        else -> {
            print(invalidInputMessage)
            createGraph()
        }
    }
    modifyGraph()
}

fun loadGraphFromFile() {
    print("Please enter file name (*.txt): ")
    val filename = readln()
    if (filename.takeLast(4) != ".txt") {
        print(invalidInputMessage)
        loadGraphFromFile()
    } else {
        try {
            val input = File(filename)
            val inputGraphType = input.readLines()[0].split(", ")
            var graphType = GraphType.UNDIRECTED
            if (inputGraphType[1] == "DIRECTED") {
                graphType = GraphType.DIRECTED
            }
            when (inputGraphType[0]) {
                "WEIGHTED" -> {
                    graph = WeightedGraph(graphType)
                    graph = (graph as WeightedGraph).readFromFile(filename)
                    modifyGraph()
                }

                "UNWEIGHTED" -> {
                    graph = UnweightedGraph(graphType)
                    graph = (graph as UnweightedGraph).readFromFile(filename)
                    modifyGraph()
                }

                else -> {
                    print("An unexpected error occurred, try again.")
                    loadGraphFromFile()
                }
            }
        } catch (exception: IOException) {
            print("File \"$filename\" does not exist, try again.\n")
            loadGraphFromFile()
        }
    }
}

fun modifyGraph() {
    print(
        "1. Add vertex.\n" +
                "2. Remove vertex.\n" +
                "3. Add edge.\n" +
                "4. Remove edge.\n" +
                "5. Print to file.\n" +
                "6. Solve task 1.\n" +
                "7. Solve task 2.\n" +
                "8. Exit.\n" +
                ">>> "
    )
    when (readln()) {
        "1" -> {
            addVertex()
            modifyGraph()
        }

        "2" -> {
            removeVertex()
            modifyGraph()
        }

        "3" -> {
            addEdge()
            modifyGraph()
        }

        "4" -> {
            removeEdge()
            modifyGraph()
        }

        "5" -> {
            printToFile()
            modifyGraph()
        }

        "6" -> {
            solveTask1()
            modifyGraph()
        }

        "7" -> {
            solveTask2()
            modifyGraph()
        }

        "8" -> return
    }
}

fun createRandomGraph() {
    print("Set vertices count: ")
    try {
        val verticesCount = readln().toInt()
        println("Please select the graph type: ")
        print("Weighted: \n\t1. Directed.\n\t2. Undirected.\nUnweighted: \n\t3.Directed.\n\t4.Undirected.\n>>> ")
        when (readln()) {
            "1" -> {
                print("Enter minimum weight: ")
                val minWeight = readln().toInt()
                print("Enter maximum weight: ")
                val maxWeight = readln().toInt()
                graph = WeightedGraph.createRandomGraph(verticesCount, minWeight, maxWeight, GraphType.DIRECTED)
                print("Graph has been successfully created.\n")
                modifyGraph()
            }

            "2" -> {
                print("Enter minimum weight: ")
                val minWeight = readln().toInt()
                print("Enter maximum weight: ")
                val maxWeight = readln().toInt()
                graph = WeightedGraph.createRandomGraph(verticesCount, minWeight, maxWeight, GraphType.UNDIRECTED)
                print("Graph has been successfully created.\n")
                modifyGraph()
            }

            "3" -> {
                graph = UnweightedGraph.createRandomGraph(verticesCount, GraphType.DIRECTED)
                print("Graph has been successfully created.\n")
                modifyGraph()

            }

            "4" -> {
                graph = UnweightedGraph.createRandomGraph(verticesCount, GraphType.UNDIRECTED)
                print("Graph has been successfully created.\n")
                modifyGraph()
            }


            else -> throw IOException()
        }
    } catch (exception: NumberFormatException) {
        print(invalidInputMessage)
        createRandomGraph()
    } catch (exception: IOException) {
        print(invalidInputMessage)
        createRandomGraph()
    }
}

fun addVertex(): Graph {
    print("\nEnter vertex name: ")
    return when (graph) {
        is WeightedGraph -> {
            try {
                graph = (graph as WeightedGraph).addVertex(Vertex(readln()))
                println("\nVertex successfully added!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                addVertex()
            }
        }

        is UnweightedGraph -> {
            try {
                graph = (graph as UnweightedGraph).addVertex(Vertex(readln()))
                println("\nVertex successfully added!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                addVertex()
            }
        }

        else -> {
            return graph
        }
    }
}

fun removeVertex(): Graph {
    print("\nEnter vertex name: ")
    return when (graph) {
        is WeightedGraph -> {
            try {
                graph = (graph as WeightedGraph).removeVertex(Vertex(readln()))
                println("\nVertex successfully removed!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                removeVertex()
            }
        }

        is UnweightedGraph -> {
            try {
                graph = (graph as UnweightedGraph).removeVertex(Vertex(readln()))
                println("\nVertex successfully removed!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                removeVertex()
            }
        }

        else -> {
            return graph
        }
    }
}

fun addEdge(): Graph {
    print("\nEnter source vertex name: ")
    val source = Vertex(readln())
    print("\nEnter destination vertex name: ")
    val destination = Vertex(readln())
    return when (graph) {
        is WeightedGraph -> {
            try {
                print("\nEnter weight: ")
                val weight = readln().toInt()
                graph = (graph as WeightedGraph).addEdge(source, destination, weight)
                println("\nEdge successfully added!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                addEdge()
            }
        }

        is UnweightedGraph -> {
            try {
                graph = (graph as UnweightedGraph).addEdge(source, destination)
                println("\nEdge successfully added!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                addEdge()
            }
        }

        else -> {
            return graph
        }
    }
}

fun removeEdge(): Graph {
    print("\nEnter source vertex name: ")
    val source = Vertex(readln())
    print("\nEnter destination vertex name: ")
    val destination = Vertex(readln())
    return when (graph) {
        is WeightedGraph -> {
            try {
                graph = (graph as WeightedGraph).removeEdge(source, destination)
                println("\nEdge successfully removed!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                removeEdge()
            }
        }

        is UnweightedGraph -> {
            try {
                graph = (graph as UnweightedGraph).removeEdge(source, destination)
                println("\nEdge successfully removed!")
                return graph
            } catch (exception: Exception) {
                print("\n${exception.message}")
                removeEdge()
            }
        }

        else -> {
            return graph
        }
    }
}

fun printToFile() {
    print("Please enter file name (*.txt): ")
    val filename = readln()
    if (filename.takeLast(4) != ".txt") {
        print(invalidInputMessage)
        printToFile()
    } else {
        graph.printToFile(filename)
        print("Graph successfully saved in \"$filename\".\n")
    }
}

fun solveTask1() {
    val result = graph.getDegrees().toList().sortedBy { it.second }.asReversed().toMap()
    println("Vertices degrees: ")
    result.forEach {
        println("${it.key}: ${it.value}.")
    }
    modifyGraph()
}

fun solveTask2() {
    print("Vertices with hinges: ${graph.getHingeVertices()}")
}
