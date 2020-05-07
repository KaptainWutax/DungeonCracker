package dungeon;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import randomreverser.RandomReverser;
import randomreverser.device.JavaRandomDevice;

public class Bruteforce extends Stage {

	public static Bruteforce instance = new Bruteforce();
	private final StackPane pane;

	private String sequence = "";

	private Bruteforce() {
		this.pane = new StackPane();
		this.setTitle("Dungeon Cracker | Bruteforce");
		this.setResizable(false);

		Scene scene = new Scene(pane, 1280, 720);
		this.setScene(scene);
	}

	public void init() {
		for(int x = 0; x < DungeonCracker.floor.floorPattern.length; x++) {
			for(int z = 0; z < DungeonCracker.floor.floorPattern[x].length; z++) {
				ImageView image = DungeonCracker.floor.floorPattern[x][z];

				if(image.getImage() == Floor.COBBLE) {
					this.sequence += "0";
				} else if(image.getImage() == Floor.MOSSY) {
					this.sequence += "1";
				} else if(image.getImage() == Floor.UNKNOWN) {
					this.sequence += "2";
				}
			}
		}

		int x = DungeonCracker.spawnerCoords.x;
		int y = DungeonCracker.spawnerCoords.y;
		int z = DungeonCracker.spawnerCoords.z;

		Label sequenceLabel = new Label("Floor Sequence: " + this.sequence);
		sequenceLabel.setFont(Font.font(16.0D));
		//sequenceLabel.setTranslateX(-200.0D);
		sequenceLabel.setTranslateY(-320.0D);

		Label localPosLabel = new Label(String.format("Local Position: (%d, %d, %d)", x & 15, y, z & 15));
		localPosLabel.setFont(Font.font(16.0D));
		//localPosLabel.setTranslateX(-500.0D);
		localPosLabel.setTranslateY(-300.0D);

		Label chunkPosLabel = new Label(String.format("Chunk Position: (%d, %d)", x >> 4, z >> 4));
		chunkPosLabel.setFont(Font.font(16.0D));
		//chunkPosLabel.setTranslateX(-500.0D);
		chunkPosLabel.setTranslateY(-280.0D);

		this.pane.getChildren().addAll(sequenceLabel, localPosLabel, chunkPosLabel);

		TextArea outputLog = new TextArea();
		outputLog.setScaleX(0.75D);
		outputLog.setScaleY(0.75D);
		outputLog.setTranslateY(40.0D);
		outputLog.setFont(Font.font(25.0D));
		this.pane.getChildren().add(outputLog);

		//Test data.
		/*
		this.sequence = "111101111111111110011101011110111011011110111101111111101110011";
		x = -1699;
		y = 38;
		z = -1465;
		*/

		JavaRandomDevice device = new JavaRandomDevice();
		device.nextInt(16, x & 15).nextInt(16, z & 15).nextInt(256, y);
		device.skipNextInt(2, 2);

		for(char c: this.sequence.toCharArray()) {
			if(c == '0') {
				device.nextInt(4, 0);
			} else if(c == '1') {
				device.filterSkip(jRand -> jRand.nextInt(4) != 0);
			} else {
				device.skipNextInt(4, 1);
			}
		}

		//TODO: Multi-thread this.
		Platform.runLater(() -> {
			outputLog.setText("Looking for dungeon seeds...");

			device.streamSeeds().forEach(seed -> {
				String t = outputLog.getText();
				outputLog.setText(t + "\n" + "Found dungeon seed " + seed + ".");
			});

			outputLog.setText(outputLog.getText() + "\n" + "Finished looking for dungeon seeds.");
		});
	}

}
