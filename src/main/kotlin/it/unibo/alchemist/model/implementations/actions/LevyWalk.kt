package it.unibo.alchemist.model.implementations.actions

import it.unibo.alchemist.model.implementations.movestrategies.RandomTarget
import it.unibo.alchemist.model.implementations.movestrategies.speed.GloballyConstantSpeed
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.implementations.routes.PolygonalChain
import it.unibo.alchemist.model.interfaces.Environment
import it.unibo.alchemist.model.interfaces.Node
import it.unibo.alchemist.model.interfaces.Reaction
import it.unibo.alchemist.model.interfaces.movestrategies.RoutingStrategy
import org.apache.commons.math3.distribution.ParetoDistribution
import org.apache.commons.math3.random.RandomGenerator

/**
 * Selects a target based on a random direction extracted from [rng] (which should be an uniform random generator),
 * and a random distance extracted from a lévy distribution of parameters [location] (aka mu) and [scale] (aka c).
 */
class LevyWalk<T>(
    node: Node<T>,
    reaction: Reaction<T>,
    private val env: Environment<T, Euclidean2DPosition>,
    private val rng: RandomGenerator,
    private val speed: Double
) : AbstractConfigurableMoveNodeWithAccurateEuclideanDestination<T>(
    env,
    node,
    RoutingStrategy { p1, p2 -> PolygonalChain<Euclidean2DPosition>(listOf(p1, p2)) },
    RandomTarget<T>(node, env, rng, ParetoDistribution(rng, 1.0, 1.0)),
    GloballyConstantSpeed(reaction, speed)
) {
    override fun cloneAction(n: Node<T>, r: Reaction<T>) =
        LevyWalk(n, r, env, rng, speed)
}