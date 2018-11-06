package com.atinem.pong

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent


class FullscreenActivity : AppCompatActivity() {

    private lateinit var pongGame : PongGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)


        pongGame = PongGame(this,size.x.toFloat(), size.y.toFloat())
        setContentView(pongGame)

    }

    override fun onPause() {
        super.onPause()
        pongGame.pause()
    }

    override fun onResume() {
        super.onResume()
        pongGame.resume()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                pongGame.mPaused = false
                if(event.x > pongGame.mScreenX / 2) pongGame.mBat.setMovementState(Bat.RIGHT)
                else pongGame.mBat.setMovementState(Bat.LEFT)
            }
            MotionEvent.ACTION_UP -> {
                Log.d("MOTIONEVENT", "Action UP")
                pongGame.mBat.setMovementState(Bat.STOPPED)
            }
        }
        return true
    }
}
