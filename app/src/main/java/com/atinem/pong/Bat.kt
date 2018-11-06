package com.atinem.pong

import android.graphics.RectF

class Bat(sx : Float, sy : Float) {

    val mRect : RectF
    private val mScreenX : Float = sx
    private val mLength : Float = mScreenX / 8
    private var mXCoord : Float = mScreenX / 2
    private val mBatSpeed : Float

    companion object {
        const val STOPPED = 0
        const val LEFT = 1
        const val RIGHT = 2
    }


    private var mBatMoving = STOPPED

    init {
        val height = sy / 40
        val mYCoord = sy - height
        mRect = RectF(mXCoord, mYCoord, mXCoord + mLength, mYCoord + height)

        mBatSpeed = mScreenX
    }

    fun setMovementState(state : Int){
        mBatMoving = state
    }

    fun update(fps : Long){
        if(mBatMoving == LEFT){
            mXCoord -= mBatSpeed / fps
        }
        if(mBatMoving == RIGHT){
            mXCoord += mBatSpeed / fps
        }

        if(mXCoord < 0){
            mXCoord = 0f
        }else if(mXCoord + mLength > mScreenX){
            mXCoord = mScreenX - mLength
        }

        mRect.left = mXCoord
        mRect.right = mXCoord + mLength
    }

}