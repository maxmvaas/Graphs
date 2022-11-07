package graph

data class Vertex(private val name: String) {
    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        return if (other is Vertex) {
            other.name == this.name
        } else false
    }

    override fun hashCode(): Int = name.hashCode()
}