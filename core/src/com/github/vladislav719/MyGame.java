package com.github.vladislav719;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.sun.jmx.snmp.tasks.Task;

public class MyGame extends ApplicationAdapter {
	
	
	Array<Rectangle> balls;
	Texture ball, basket;
	Music themeM;
	Sound drop;
	
	SpriteBatch batch;
	OrthographicCamera camera;
	Rectangle basketR;
	long lastDropBall;
	int loseBall = 3;
	int countBall;
	
	BitmapFont font;
	
	@Override
	public void create () {
		ball = new Texture(Gdx.files.internal("ball.png"));
		basket = new Texture(Gdx.files.internal("Basketball_Hoop.png"));
		drop = Gdx.audio.newSound(Gdx.files.internal("shot.wav"));
		themeM = Gdx.audio.newMusic(Gdx.files.internal("theme.mp3"));
		
		themeM.setLooping(true);
		themeM.play();
		
		camera= new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.setScale(1f);
		font.setColor(Color.WHITE);

		
		basketR = new Rectangle();
		basketR.width = basket.getWidth();
		basketR.height = basket.getHeight();
		basketR.x = Gdx.graphics.getWidth() / 2 - basket.getWidth() / 2;
		basketR.y = 20;
		
		balls = new Array<Rectangle>();
		ballSpawn();
		
		
	}
	public void pause ( ) {
		try {
			Thread.sleep ( 1000 ) ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	@Override
	public void render () {
		Gdx.gl20.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.draw(batch, countBall + "", 50, 50);
		batch.draw(basket, basketR.x, basketR.y);
		for (Rectangle ballR : balls) 
			batch.draw(ball, ballR.x, ballR.y);
		batch.end();
		
		if(Gdx.input.isKeyPressed(Keys.W)) {
			basketR.y += 230 * Gdx.graphics.getDeltaTime();
		}
		if(Gdx.input.isKeyPressed(Keys.S)) {
			basketR.y -= 230 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Keys.X)) {
			//basketR.width *= 2;
			//basketR.height *= 2;
			ball = new Texture(Gdx.files.internal("Basketball_Hoop.png"));
			basket = new Texture(Gdx.files.internal("ball.png"));
			
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)){
			basketR.x -= 300 * Gdx.graphics.getDeltaTime();
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)){
			basketR.x +=300 * Gdx.graphics.getDeltaTime();
		}
		
		if (basketR.x < 0) basketR.x = 0;
		if (basketR.x > Gdx.graphics.getWidth() - 64) basketR.x = Gdx.graphics.getWidth() - 64;
		
		if (TimeUtils.nanoTime() - lastDropBall > 1000000000) ballSpawn();
		
		Iterator<Rectangle> iter = balls.iterator();
		while (iter.hasNext()) {
			Rectangle ballRe =  iter.next();
			ballRe.y -= 200 * Gdx.graphics.getDeltaTime();
			if (ballRe.y + 64 < 0) {
				loseBall--;
				iter.remove();
			}
			if (ballRe.overlaps(basketR)){
				countBall++;
				drop.play();
				iter.remove();
			}
			
		}
		
		if (loseBall < 1) {
			batch.begin();
			balls.clear();
			font.draw(batch, "Game Over", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			//pause ( );
			/*Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
				}
			} );
			try {
				t.sleep( 1000 );
				iter = balls.iterator();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t.start();
			*/
			batch.flush();
			loseBall = 3;
			countBall = 0;
			batch.end();
			font.dispose();
		}
		
		
	}
	@Override
	public void dispose(){
		ball.dispose();
		basket.dispose();
		drop.dispose();
		themeM.dispose();
		batch.dispose();
	}
	
	private void ballSpawn() {
		Rectangle ballR= new Rectangle();
		ballR.x = MathUtils.random(0, Gdx.graphics.getWidth() -  64);
		ballR.y = Gdx.graphics.getHeight();
		ballR.width = 64;
		ballR.height = 64;
		balls.add(ballR);
		lastDropBall = TimeUtils.nanoTime();
	}
}
