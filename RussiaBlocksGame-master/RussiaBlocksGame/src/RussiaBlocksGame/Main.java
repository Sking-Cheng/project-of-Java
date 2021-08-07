package RussiaBlocksGame;

/**
 * 程序入口函数
 *
 * @param args String[],附带的命令行参数
 */

public class Main {
	public static void main(String[] args) {
		String filepath = "D:\\cc.wav";
        musicStuff musicObject = new musicStuff();
        musicObject.playMusic(filepath);
        new RussiaBlocksGame("俄罗斯方块");
    }
	
}
