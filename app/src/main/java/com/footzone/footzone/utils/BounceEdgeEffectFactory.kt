package com.footzone.footzone.utils

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.5f

private const val FLING_TRANSLATION_MAGNITUDE = 0.7f

class BounceEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {

        return object : EdgeEffect(recyclerView.context) {

            var translationAnim: SpringAnimation? = null

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationYDelta =
                    sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                recyclerView.translationY += translationYDelta

                translationAnim?.cancel()
            }

            override fun onRelease() {
                super.onRelease()
                // The finger is lifted. Start the animation to bring translation back to the resting state.
                if (recyclerView.translationY != 0f) {
                    translationAnim = createAnim()?.also { it.start() }
                }


            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)

                // The list has reached the edge on fling.
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                translationAnim?.cancel()
                translationAnim =
                    createAnim().setStartVelocity(translationVelocity)?.also { it.start() }
            }

            override fun draw(canvas: Canvas?): Boolean {
                // don't paint the usual edge effect
                return false
            }

            override fun isFinished(): Boolean {
                // Without this, will skip future calls to onAbsorb()
                return translationAnim?.isRunning?.not() ?: true
            }

            private fun createAnim() = SpringAnimation(recyclerView, SpringAnimation.TRANSLATION_Y)
                .setSpring(
                    SpringForce()
                        .setFinalPosition(0f)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
//                    .setStiffness(SpringForce.STIFFNESS_HIGH)
                        .setStiffness(8000f)
                )

        }
    }
}