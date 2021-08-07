package RussiaBlocksGame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * 游戏主类，继承自JFrame类，负责游戏的全局控制。 内含： 
 * 1.一个GameCanvas画布类的实例对象，
 * 2.一个保存当前活动块（RussiaBlock）实例的对象； 
 * 3.一个保存当前控制面板（ControlPanel）实例的对象；
 */
public class RussiaBlocksGame extends JFrame {
    public final static int PER_LINE_SCORE = 100;
    /**
     * 积多少分以后能升级
     */
    public final static int PER_LEVEL_SCORE = PER_LINE_SCORE * 20;
    /**
     * 最大级数是10级
     */
    public final static int MAX_LEVEL = 10;
    /**
     * 默认级数是1
     */
    public final static int DEFAULT_LEVEL = 1;
    //画布类
    private GameCanvas canvas;
    //块类
    private ErsBlock block;
    //初始化定义bool值，表示未开始
    private boolean playing = false;
    //控制面板类
    private ControlPanel ctrlPanel;
    
    //初始化菜单栏
    private JMenuBar bar = new JMenuBar();
    private JMenu mGame = new JMenu(" 游戏"),
            mControl = new JMenu(" 控制"),
            mInfo = new JMenu("帮助");
    private JMenuItem miNewGame = new JMenuItem("新游戏"),
            miSetBlockColor = new JMenuItem("设置方块颜色..."),
            miSetBackColor = new JMenuItem("设置背景颜色..."),
            miTurnHarder = new JMenuItem("升高游戏难度"),
            miTurnEasier = new JMenuItem("降低游戏难度"),
            miExit = new JMenuItem("退出"),
            
            miPlay = new JMenuItem("开始"),
            miPause = new JMenuItem("暂停"),
            miResume = new JMenuItem("恢复"),
            miStop = new JMenuItem("终止游戏"),
            
            miRule = new JMenuItem("游戏规则"),
            miAuthor = new JMenuItem("关于本游戏");
    		
    
    /**
     * 建立并设置窗口菜单
     */
    private void creatMenu() {
        bar.add(mGame);
        bar.add(mControl);
        bar.add(mInfo);
        
        //游戏栏
        mGame.add(miNewGame);
        mGame.addSeparator();
        mGame.add(miSetBlockColor);
        mGame.add(miSetBackColor);
        mGame.addSeparator();
        mGame.add(miTurnHarder);
        mGame.add(miTurnEasier);
        mGame.addSeparator();
        mGame.add(miExit);
        
        //控制栏
        mControl.add(miPlay);
        miPlay.setEnabled(true);
        mControl.add(miPause);
        miPause.setEnabled(false);
        mControl.add(miResume);
        miResume.setEnabled(false);
        mControl.add(miStop);
        miStop.setEnabled(false);
        
        //帮助栏
        mInfo.add(miRule);
        mInfo.add(miAuthor);
        setJMenuBar(bar);

        //设置新游戏监听
        miNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGame();
                reset();
                setLevel(DEFAULT_LEVEL);
            }
        });
        
        //设置方块颜色监听
        miSetBlockColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newFrontColor =
                        JColorChooser.showDialog(RussiaBlocksGame.this, 
                        		"设置方块颜色", canvas.getBlockColor());
                if (newFrontColor != null) {
                    canvas.setBlockColor(newFrontColor);
                }
            }
        });
        
        //设置背景颜色监听
        miSetBackColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newBackColor =
                        JColorChooser.showDialog(RussiaBlocksGame.this, 
                        		"设置背景颜色", canvas.getBackgroundColor());
                if (newBackColor != null) {
                    canvas.setBackgroundColor(newBackColor);
                }
            }
        });
                
        //增加难度监听
        miTurnHarder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int curLevel = getLevel();
                if (!playing && curLevel < MAX_LEVEL) {
                    setLevel(curLevel + 1);
                }
            }
        });
        
        //减少难度监听
        miTurnEasier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int curLevel = getLevel();
                if (!playing && curLevel > 1) {
                    setLevel(curLevel - 1);
                }
            }
        });
        
        //退出监听
        miExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
     
        //开始监听
        miPlay.addActionListener(new ActionListener() {                         //开始游戏
            @Override
            public void actionPerformed(ActionEvent ae) {
                playGame();
            }
        });
        
        //恢复监听
        miResume.addActionListener(new ActionListener() {                         
            @Override
            public void actionPerformed(ActionEvent ae) {
                resumeGame();
            }
        });
        
        //暂停监听
        miPause.addActionListener(new ActionListener() {                         
            @Override
            public void actionPerformed(ActionEvent ae) {
                pauseGame();
            }
        });
        
        //终止监听
        miStop.addActionListener(new ActionListener() {                         
            @Override
            public void actionPerformed(ActionEvent ae) {
                stopGame();
            }
        });    
        
        //定义菜单栏"关于"的功能监听
        miAuthor.addActionListener(new ActionListener() {                        
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, 
                		"达内教育\n实训第三组\n©一切解释权归本组团队所有", "关于本游戏", 1);
            }
        });    
        
        //游戏规则说明
        miRule.addActionListener(new ActionListener() {                        
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, 
                		"由小方块组成的不同形状的板块陆续从屏幕上方落下来，\n玩家通过调整板块的位置和方向，使它们在屏幕底部拼"
                		+ "\n出完整的一条或几条。这些完整的横条会随即消失，给新\n落下来的板块腾出空间，与此同时，玩家得到分数奖励。"
                		+ "\n没有被消除掉的方块不断堆积起来，一旦堆到屏幕顶端，\n玩家便告输，游戏结束。", "游戏规则", 1);
            }
        });    
}      
    
    
    /**
     * 主游戏类的构造方法
     *
     * title String ,窗口标题
     */
    public RussiaBlocksGame(String title) {
        super(title);                                          //设置标题
        setSize(500, 600);                                     //设置窗口大小                  
        setLocationRelativeTo(null);                             //设置窗口居中

        creatMenu();
        Container container = getContentPane();					  //获取内容面板
        container.setLayout(new BorderLayout(6, 0));              //设置窗口的布局管理器
        canvas = new GameCanvas(20, 15);                          //新建游戏画布
        ctrlPanel = new ControlPanel(this);                        //新建控制面板
        container.add(canvas, BorderLayout.CENTER);                //左边加上画布
        container.add(ctrlPanel, BorderLayout.EAST);               //右边加上控制面板

        //注册窗口事件。当点击关闭按钮时，结束游戏，系统退出。
        addWindowListener(new WindowAdapter() {                    
            @Override
            public void windowClosing(WindowEvent we) {
                stopGame();
                System.exit(0);
            }
        });
        
        //根据窗口大小，自动调节方格的尺寸
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                canvas.adjust();
            }
        });

        setVisible(true);
    }

    /**
     * 让游戏复位
     */
    public void reset() {                            //画布复位，控制面板复位
    	//设置右侧控制面板中各个按钮的状态
        ctrlPanel.setPlayButtonEnable(true);
        ctrlPanel.setPauseButtonEnable(false);
        ctrlPanel.setPauseButtonLabel(true);
        ctrlPanel.setStopButtonEnable(false);
        ctrlPanel.setTurnLevelDownButtonEnable(true);
        ctrlPanel.setTurnLevelUpButtonEnable(true);
        
        
        //设置控制中各个按钮的状态
        miPlay.setEnabled(true);
        miPause.setEnabled(false);
        miResume.setEnabled(false);
        miStop.setEnabled(false);
        ctrlPanel.reset();
        canvas.reset();
    }

    /**
     * 判断游戏是否还在进行
     *
     * boolean,true -还在运行，false-已经停止
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * 得到当前活动的块
     *
     *ErsBlock,当前活动块的引用
     */
    public ErsBlock getCurBlock() {
        return block;
    }

    /**
     * 得到当前画布
     *
     * GameCanvas,当前画布的引用
     */
    public GameCanvas getCanvas() {
        return canvas;
    }

    /**
     * 开始游戏
     */
    public void playGame() {
        play();
        ctrlPanel.setPlayButtonEnable(false);
        ctrlPanel.setPauseButtonEnable(true);
        ctrlPanel.setPauseButtonLabel(true);
        ctrlPanel.setStopButtonEnable(true);
        ctrlPanel.setTurnLevelDownButtonEnable(false);
        ctrlPanel.setTurnLevelUpButtonEnable(false);
        miStop.setEnabled(true);
        miTurnHarder.setEnabled(false);
        miTurnEasier.setEnabled(false);
        ctrlPanel.requestFocus();              //设置焦点
    }

    /**
     * 游戏暂停
     */
    public void pauseGame() {
        if (block != null) {
            block.pauseMove();
        }
        
        //设置右侧控制面板中各个按钮的状态
        ctrlPanel.setPlayButtonEnable(false);
        ctrlPanel.setPauseButtonLabel(false);
        ctrlPanel.setStopButtonEnable(true);
        
        //设置控制中各个按钮的状态
        miPlay.setEnabled(false);
        miPause.setEnabled(false);
        miResume.setEnabled(true);
        miStop.setEnabled(true);
    }

    /**
     * 让暂停中的游戏继续
     */
    public void resumeGame() {
        if (block != null) {
            block.resumeMove();
        }
        
        //设置右侧控制面板中各个按钮的状态
        ctrlPanel.setPlayButtonEnable(false);
        ctrlPanel.setPauseButtonEnable(true);
        ctrlPanel.setPauseButtonLabel(true);
        
        //设置控制中各个按钮的状态
        miPause.setEnabled(true);
        miResume.setEnabled(false);
        ctrlPanel.requestFocus();
    }

    /**
     * 用户停止游戏
     */
    public void stopGame() {
        playing = false;
        if (block != null) {
            block.stopMove();
        }
        
        //设置右侧控制面板中各个按钮的状态
        ctrlPanel.setPlayButtonEnable(true);
        ctrlPanel.setPauseButtonEnable(false);
        ctrlPanel.setPauseButtonLabel(true);
        ctrlPanel.setStopButtonEnable(false);
        ctrlPanel.setTurnLevelDownButtonEnable(true);
        ctrlPanel.setTurnLevelUpButtonEnable(true);
        
        //设置控制中各个按钮的状态
        miPlay.setEnabled(true);
        miPause.setEnabled(false);
        miResume.setEnabled(false);
        miStop.setEnabled(false);
        miTurnHarder.setEnabled(true);
        miTurnEasier.setEnabled(true);
        reset();//重置画布和控制面板
    }


    /**
     * 得到游戏者设置的难度
     *
     * int ，游戏难度1-MAX_LEVEL
     */
    public int getLevel() {
        return ctrlPanel.getLevel();
    }

    /**
     * 用户设置游戏难度
     *
     *level int ，游戏难度1-MAX_LEVEL
     */
    public void setLevel(int level) {
        if (level < 11 && level > 0) {
            ctrlPanel.setLevel(level);
        }
    }

    /**
     * 得到游戏积分
     *
     *int，积分
     */
    public int getScore() {
        if (canvas != null) {
            return canvas.getScore();
        }
        return 0;
    }

    /**
     * 得到自上次升级以来的游戏积分，升级以后，此积分清零
     *
     * int,积分
     */
    public int getScoreForLevelUpdate() {
        if (canvas != null) {
            return canvas.getScoreForLevelUpdate();
        }
        return 0;
    }

    /**
     * 当积分累积到一定数值时，升一次级
     *
     * Boolean，true-update succeed，false-update fail
     */
    public boolean levelUpdate() {
        int curLevel = getLevel();
        if (curLevel < MAX_LEVEL) {
            setLevel(curLevel + 1);
            canvas.resetScoreForLevelUpdate();
            return true;
        }
        return false;
    }

    /**
     * 游戏开始
     */
    private void play() {
        reset();
        playing = true;
        Thread thread = new Thread(new Game());//启动游戏线程
        thread.start();
    }

    /**
     * 报告游戏结束了
     */
    private void reportGameOver() {
        new gameOverDialog(this, "俄罗斯方块", "游戏结束，您的得分为" + canvas.getScore());
    }


    /**
     * 一轮游戏过程，实现了Runnable接口 一轮游戏是一个大循环，在这个循环中，每隔500毫秒， 检查游戏中的当前块是否已经到底了，如果没有，
     * 就继续等待。如果到底了，就看有没有全填满的行， 如果有就删除它，并为游戏者加分，同时随机产生一个新的当前块并让它自动落下。
     * 当新产生一个块时，先检查画布最顶上的一行是否已经被占了，如果是，可以判断Game Over 了。
     */
private class Game implements Runnable {
       @Override
        public void run() {
    	   
    	    //方块落下的x轴位置
            int col = (int) (Math.random() * (canvas.getCols() - 3));
            //方块的形状
            int style = ErsBlock.STYLES[ (int) (Math.random() * 7)][(int) (Math.random() * 4)];

            while (playing) {
                if (block != null) {   //第一次循环时，block为空
                    if (block.isAlive()) {
                        try {
                            Thread.currentThread();//返回正在进行的线程
    						Thread.sleep(500);  //线程休眠时间（下一个方块落下的时间延迟）
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                        continue;
                    }
                }

                checkFullLine();    //检查是否有全填满的行，如果有就删之
                
                
                //当游戏结束时
                if (isGameOver()) {
                    reportGameOver();
                    
                    //设置右侧控制面板中各个按钮的状态
                    ctrlPanel.setPlayButtonEnable(true);
                    ctrlPanel.setPauseButtonLabel(false);
                    ctrlPanel.setStopButtonEnable(false);
                    
                    //设置控制中各个按钮的状态
                    miPlay.setEnabled(true);
                    miPause.setEnabled(false);
                    miResume.setEnabled(false);
                    miStop.setEnabled(false);
                    return;
                }
                
                //新的方块
                block = new ErsBlock(style, -1, col, getLevel(), canvas);
                //线程启动
                block.start();
                
                //下一个方块落下的x轴位置
                col = (int) (Math.random() * (canvas.getCols() - 3));
                
                //下一个方块的形状
                style = ErsBlock.STYLES[ (int) (Math.random() * 7)][(int) (Math.random() * 4)];
                
                //下一个方块的形状在控制面板中显示
                ctrlPanel.setTipStyle(style); 
            }
        }

        //检查画布中是否有全填满的行，如果有就删之
        public void checkFullLine() {
        	
        	//按照行数依次检查
            for (int i = 0; i < canvas.getRows(); i++) {
                int row = -1;
                boolean fullLineColorBox = true;    //初始化为true
                for (int j = 0; j < canvas.getCols(); j++) {
                	//若某一整行没有被填满，返回false
                    if (!canvas.getBox(i, j).isColorBox()) {
                        fullLineColorBox = false;
                        break;
                    }
                }
                
                //发现有全满的就删除
                if (fullLineColorBox) {
                    row = i--;
                    canvas.removeLine(row);//清除整行
                }
            }
        }

        //根据最顶行是否被占，判断游戏是否已经结束了
        //true-游戏结束了，false-游戏未结束
        private boolean isGameOver() {
            for (int i = 0; i < canvas.getCols(); i++) {
                ErsBox box = canvas.getBox(0, i);
                if (box.isColorBox()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 定义GameOver对话框。
     */
    private class gameOverDialog extends JDialog implements ActionListener {
    	
    	
    	//定义按钮
        private JButton againButton, exitButton;
        //定义边框
        private Border border = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));

        public gameOverDialog(JFrame parent, String title, String message) {
            super(parent, title, true);
            if (parent != null) {
                setSize(240, 120);                                 //框的大小
                this.setLocationRelativeTo(parent);                //设置窗口相对于指定组件的位置
                
                JPanel messagePanel = new JPanel();                //创建信息面板
                JPanel choosePanel = new JPanel();                 //创建选择面板
                messagePanel.add(new JLabel(message));             //显示内容（得分）
                messagePanel.setBorder(border);
                
                
                Container container = this.getContentPane();       //获取内容面板
                container.setLayout(new GridLayout(2, 0, 0, 10));  //设置窗口布局
                container.add(messagePanel);                       //添加信息面板
                choosePanel.setLayout(new GridLayout(0, 2, 4, 0)); //设置窗口布局
                container.add(choosePanel);                        //添加选择面板
                
                //添加按钮并加入选择面板
                againButton = new JButton("再玩一局");
                exitButton = new JButton("退出游戏");
                choosePanel.add(new JPanel().add(againButton));
                choosePanel.add(new JPanel().add(exitButton));
                choosePanel.setBorder(border);
            }
            
            
            //添加监听事件
            againButton.addActionListener(this);
            exitButton.addActionListener(this);
            this.setVisible(true);
        }
        
        
        //定义监听事件
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == againButton) {
                this.setVisible(false);
                reset();
            } else if (e.getSource() == exitButton) {
                stopGame();
                System.exit(0);

            }
        }
    }
}