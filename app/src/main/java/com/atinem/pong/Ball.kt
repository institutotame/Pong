package com.atinem.pong

import android.graphics.RectF

class Ball(screenX : Float) {
    val mRect : RectF = RectF()
    private var mXVelocity : Float = 0f
    private var mYVelocity : Float = 0f
    private val mBallWidth : Float = screenX / 100
    private val mBallHeight : Float = screenX / 100

    fun update(fps : Long){
        mRect.left += (mXVelocity/fps)
        mRect.top += (mYVelocity/fps)

        mRect.right = mRect.left + mBallWidth
        mRect.bottom = mRect.top + mBallHeight
    }

    fun reverseYVelocity(){
        mYVelocity = -mYVelocity
    }

    fun reverseXVelocity(){
        mXVelocity = -mXVelocity
    }

    fun reset(x : Float, y : Float) {
        mRect.left = (x / 2)
        mRect.top = 0f
        mRect.right = x/2 + mBallWidth
        mRect.bottom = mBallHeight

        mYVelocity = -(y/3)
        mXVelocity = (y/3)
    }

    fun increaseVelocity(){
        mXVelocity *= 1.1f
        mYVelocity *= 1.1f
    }

    fun batBounce(){
        reverseYVelocity()
    }

}