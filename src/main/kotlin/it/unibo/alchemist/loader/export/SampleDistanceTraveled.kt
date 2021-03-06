package it.unibo.alchemist.loader.export

import com.google.common.collect.MapMaker
import it.unibo.alchemist.model.interfaces.Environment
import it.unibo.alchemist.model.interfaces.Molecule
import it.unibo.alchemist.model.interfaces.Node
import it.unibo.alchemist.model.interfaces.Position
import it.unibo.alchemist.model.interfaces.Reaction
import it.unibo.alchemist.model.interfaces.Time

/**
 * Extracts the sum of the distances traveled by each node optionally filtering the ones containing [filterByMolecule].
 */
class SampleDistanceTraveled<T, P : Position<P>> @JvmOverloads constructor(
    private val exportName: String = "distance",
    private val filterByMolecule: Molecule? = null
) : Extractor {
    // note: MapMaker makes ConcurrentMaps by default
    private val lastPositionMap = MapMaker().weakKeys().makeMap<Environment<T, P>, MutableMap<Node<T>, P>>()

    override fun extractData(environment: Environment<*, *>, r: Reaction<*>?, time: Time?, step: Long): DoubleArray {
        @Suppress("UNCHECKED_CAST") val env = environment as Environment<T, P>
        val newMap = mutableMapOf<Node<T>, P>()
        val lastPositions = lastPositionMap.putIfAbsent(environment, newMap) ?: newMap
        var nodes: List<Node<T>> = env.nodes
        filterByMolecule?.also { molecule ->
            nodes = nodes.filter { it.contains(molecule) }
        }
        return nodes.map {
            val currentPos = env.getPosition(it)
            val distance = lastPositions[it]?.getDistanceTo(currentPos) ?: 0.0
            lastPositions[it] = currentPos
            distance
        }.sum().let { doubleArrayOf(it) }
    }

    override fun getNames() = listOf(exportName)
}