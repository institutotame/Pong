package com.atinem.pong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class PongGame(context: Context?, x : Float, y : Float) : SurfaceView(context) {
    private val DEBUGGING = true
    private val mOurHolder : SurfaceHolder
    private val mPaint : Paint

    private var mFPS : Long = 0
    private var minFPS : Long = 1000
    private var maxFPS : Long = 0
    private val MILLIS_IN_SECOND : Long = 1000

    val mScreenX : Float = x
    private val mScreenY : Float = y

    private val mFontSize : Float
    private val mFontMargin : Float

    private var mScore : Int = 0
    private var mLives : Int = 0

    //Sound variables
    private val mSP : SoundPool
    private var mBeepID = -1
    private var mBoopID = -1
    private var mBopID = -1
    private var mMissID = -1

    private var job : Job? = null

    var mPlaying = false
    var mPaused = true

    val mBat : Bat
    private val mBall : Ball

    init {

        mFontSize = (mScreenX / 20)

        mFontMargin = (mScreenX / 75)

        mOurHolder = holder
        mPaint = Paint()

        mBall = Ball(mScreenX)
        mBat = Bat(mScreenX,mScreenY)

        mSP = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build()
        }else{
            SoundPool(5,AudioManager.STREAM_MUSIC,0)
        }

        try{
            val assetManager = context?.assets
            var descriptor = assetManager?.openFd("beep.ogg")
            mBeepID = mSP.load(descriptor, 0)

            descriptor = assetManager?.openFd("boop.ogg")
            mBoopID = mSP.load(descriptor, 0)

            descriptor = assetManager?.openFd("bop.ogg")
            mBopID = mSP.load(descriptor, 0)

            descriptor = assetManager?.openFd("miss.ogg")
            mMissID = mSP.load(descriptor, 0)
        }catch (e : IOException){
            Log.e("error", "failed to load sound files")
        }

        startNewGame()
    }

    private fun startNewGame(){

        mBall.reset(mScreenX,mScreenY)

        mScore = 0
        mLives = 3
    }

    private fun draw() {
        if(mOurHolder.surface.isValid){
            val canvas = mOurHolder.lockCanvas()
            canvas.drawColor(Color.argb(255,26,128,182))
            mPaint.color = Color.argb(255, 255, 255, 255)

            canvas.drawRect(mBall.mRect, mPaint)
            canvas.drawRect(mBat.mRect,mPaint)

            mPaint.textSize = mFontSize
            canvas.drawText("Score: $mScore Lives: $mLives", mFontMargin, mFontSize,mPaint)

            if(DEBUGGING){
                printDebuggingText(canvas)
            }

            mOurHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun printDebuggingText(canvas : Canvas){
        val debugSize = mFontSize / 2
        val debugStart = 150
        mPaint.textSize = debugSize
        canvas.drawText("FPS: $mFPS,   MIN: $minFPS,    MAX: $maxFPS", 10f, debugStart + debugSize, mPaint)
    }

    private fun launchJob(){
        job = GlobalScope.launch {
            while (mPlaying){
                val frameStartTime = System.currentTimeMillis()
                if(!mPaused){
                    update()
                    detectCollisions()
                }
                val drawJob = this.launch {
                    draw()
                    val timeThisFrame = System.currentTimeMillis() - frameStartTime

                    if(timeThisFrame>0){
                        mFPS = MILLIS_IN_SECOND / timeThisFrame
                        if(!mPaused){
                            if(mFPS > maxFPS){
                                maxFPS = mFPS
                            }
                            if(mFPS < minFPS){
                                minFPS = mFPS
                            }
                        }
                    }
                }
                drawJob.join()
            }
        }
    }

    fun pause() {
        mPlaying = false
        job?.cancel()
    }

    fun resume() {
        mPlaying = true
        launchJob()
    }

    private fun update(){
        mBall.update(mFPS)
        mBat.update(mFPS)
    }

    private fun detectCollisions(){
        if(RectF.intersects(mBat.mRect, mBall.mRect)){
            mBall.batBounce()
            mBall.increaseVelocity()
            mScore++
            mSP.play(mBeepID,1f,1f,0,0,1f)
        }

        if(mBall.mRect.bottom > mScreenY){
            mBall.reverseYVelocity()
            mLives--
            mSP.play(mMissID, 1f, 1f, 0, 0, 1f)
            if(mLives == 0){
                mPaused = true
                startNewGame()
            }
        }

        if(mBall.mRect.top < 0){
            mBall.reverseYVelocity()
            mSP.play(mBoopID, 1f, 1f, 0, 0, 1f)
        }

        if(mBall.mRect.left <0 || mBall.mRect.right > mScreenX){
            mBall.reverseXVelocity()
            mSP.play(mBopID, 1f, 1f, 0, 0, 1f)
        }

    }
}