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
 * �ɻ���Ϸ������
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
	int period;//��Ϸ������ʱ��

	@Override
	public void paint(Graphics g) {// �Զ����á� g�൱��һ֧����
		g.drawImage(bgImg, 0, 0, null);

		plane.drawSelf(g);// ���ɻ�

		// ���������ڵ�
		for (int i = 0; i < shells.length; i++) {
			shells[i].draw(g);

			boolean peng = shells[i].getRect().intersects(plane.getRect());

			// �ɻ����ڵ�����ײ���
			if (peng) {
				plane.live = false;
				if (bao == null) {
					bao = new Explode(plane.x, plane.y);
					
					endTime = new Date();
					period = (int)((endTime.getTime()-startTime.getTime())/1000);
				}
				
				bao.draw(g);//��ը��Ч 
			}
			
			//��ʱ���ܣ�������ʾ
			if(!plane.live) {
				Font f = new Font("����",Font.BOLD,50);
				g.setFont(f);
				//g.drawString("ʱ�䣺"+period+"��", (int)plane.x, (int)plane.y);
				g.drawString("ʱ�䣺"+period+"��", 125, 250);//��ʾ���ʱ��
			}
		}
	}

	// �������Ƿ����ػ�����
	class PaintThread extends Thread {
		@Override
		public void run() {
			while (true) {
				repaint();// �ػ�
				// System.out.println("��");

				try {
					Thread.sleep(30);// 1s = 1000ms,���25��
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// ������̼������ڲ���
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

	// ��ʼ������
	public void launchFrame() {
		this.setTitle("������Ʒ");
		this.setVisible(true);
		this.setSize(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
		this.setLocation(230, 230);// ��Ϸ����λ��

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		new PaintThread().start();// �����ػ����ڵ��߳�
		addKeyListener(new KeyMonitor());// ���Ӽ��̵ļ���

		// ��ʼ��50���ڵ�
		for (int i = 0; i < shells.length; i++) {
			shells[i] = new Shell();
		}
	}

	// ˫��������������
	private Image offScreenImage = null;

	public void update(Graphics g) {
		if (offScreenImage == null)
			offScreenImage = this.createImage(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);// ������Ϸ���ڵĿ�Ⱥ͸߶�

		Graphics gOff = offScreenImage.getGraphics();
		paint(gOff);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public static void main(String[] args) {
		MyGameFrame f = new MyGameFrame();
		f.launchFrame();
	}
}
