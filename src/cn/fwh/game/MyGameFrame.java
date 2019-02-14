package cn.fwh.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JFrame;

/**
 * 飞机游戏主窗口
 * 
 * @author Administrator
 *
 */

public class MyGameFrame extends JFrame {

	Image bgImg = GameUtil.getImage("images/bg.jpg");
	Image planeImg = GameUtil.getImage("images/plane.png");

	Plane plane = new Plane(planeImg, 250, 250);
	Shell shells[] = new Shell[10];

	Explode bao;
	Date startTime = new Date();
	Date endTime;
	int period;//游戏持续的时间

	@Override
	public void paint(Graphics g) {// 自动调用。 g相当于一支画笔
		g.drawImage(bgImg, 0, 0, null);

		plane.drawSelf(g);// 画飞机

		// 画出所有炮弹
		for (int i = 0; i < shells.length; i++) {
			shells[i].draw(g);

			boolean peng = shells[i].getRect().intersects(plane.getRect());

			// 飞机和炮弹的碰撞检测
			if (peng) {
				plane.live = false;
				if (bao == null) {
					bao = new Explode(plane.x, plane.y);
					
					endTime = new Date();
					period = (int)((endTime.getTime()-startTime.getTime())/1000);
				}
				
				bao.draw(g);//爆炸特效 
			}
			
			//计时功能，给出提示
			if(!plane.live) {
				Font f = new Font("宋体",Font.BOLD,50);
				g.setFont(f);
				//g.drawString("时间："+period+"秒", (int)plane.x, (int)plane.y);
				g.drawString("时间："+period+"秒", 125, 250);//显示存活时间
			}
		}
	}

	// 帮助我们反复重画窗口
	class PaintThread extends Thread {
		@Override
		public void run() {
			while (true) {
				repaint();// 重画
				// System.out.println("画");

				try {
					Thread.sleep(30);// 1s = 1000ms,大概25次
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// 定义键盘监听的内部类
	class KeyMonitor extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			plane.addDirection(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			plane.minusDirection(e);
		}

	}

	// 初始化窗口
	public void launchFrame() {
		this.setTitle("日天作品");
		this.setVisible(true);
		this.setSize(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
		this.setLocation(230, 230);// 游戏窗口位置

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		new PaintThread().start();// 启动重画窗口的线程
		addKeyListener(new KeyMonitor());// 增加键盘的监听

		// 初始化50个炮弹
		for (int i = 0; i < shells.length; i++) {
			shells[i] = new Shell();
		}
	}

	// 双缓冲解决闪的问题
	private Image offScreenImage = null;

	public void update(Graphics g) {
		if (offScreenImage == null)
			offScreenImage = this.createImage(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);// 这是游戏窗口的宽度和高度

		Graphics gOff = offScreenImage.getGraphics();
		paint(gOff);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public static void main(String[] args) {
		MyGameFrame f = new MyGameFrame();
		f.launchFrame();
	}
}
