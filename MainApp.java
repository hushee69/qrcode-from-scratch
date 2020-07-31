/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application
{
	private static final int MIN_WINDOW_WIDTH = 1200;
	private static final int MIN_WINDOW_HEIGHT = 800;
	
	private static final int NUM_OBJECTS = 7;
	
	private Matrix m1;
	private BinaryDecodedData bdd;
	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		VBox root = new VBox();
		
		TextField messageInput = new TextField();
		TextField decodedInput = new TextField();
		Button encoderButton = new Button("Generate code");
		Button addRemoveMask = new Button("Add/Remove mask");
		Button decoderButton = new Button("Decode now");
		Label decodedPolynomial = new Label();
		
		messageInput.setPromptText("Enter your message here..");
		
		root.getChildren().add(messageInput);
		root.getChildren().add(encoderButton);
		root.getChildren().add(addRemoveMask);
		root.getChildren().add(decodedInput);
		root.getChildren().add(decoderButton);
		root.getChildren().add(decodedPolynomial);

		Canvas canvas = new Canvas(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		int wh = 10;
		
		addRemoveMask.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0)
			{
				if( root.getChildren().size() >= NUM_OBJECTS )
				{
					if( m1 != null )
					{
						if( m1.getMasked() )
						{
							m1.removeMask();
							m1.setMasked(false);
							root.getChildren().remove(NUM_OBJECTS - 1);
							renderCode(gc, m1.getMatrixSize(), wh);
							root.getChildren().add(canvas);
							System.err.println("UNMASKED matrix");
							System.err.println(m1);
						}
						else
						{
							m1.addMask();
							m1.setMasked(true);
							root.getChildren().remove(NUM_OBJECTS - 1);
							renderCode(gc, m1.getMatrixSize(), wh);
							root.getChildren().add(canvas);
							System.err.println("MASKED matrix");
							System.err.println(m1);
						}
					}
				}
			}
		});
		
		encoderButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent evt)
			{
				final String input = messageInput.getText();
				if( input.length() > 0 )
				{
					if( root.getChildren().size() >= NUM_OBJECTS )
					{
						root.getChildren().remove(NUM_OBJECTS - 1);
					}
					
					// encode the data and put it in polynomial form
					RSEncoder rsEncoder = new RSEncoder(input);
					int[] rsEncoded = rsEncoder.encode();
					
					// binary encoded data - convert each polynomial value into binary representation
					BinaryEncodedData bed = new BinaryEncodedData(rsEncoded);
					m1 = new Matrix(bed);
					System.err.println("INFO: Matrix size is " + m1.getMatrixSize());
					
					// add the mask
					m1.addMask();
					m1.setMasked(true);
					System.err.println("Encoded: " + bed.encodedString());
					System.err.println("MASKED matrix");
					System.err.println(m1);
					
					renderCode(gc, m1.getMatrixSize(), wh);
					
					root.getChildren().add(canvas);

					System.err.println("input: " + bed.toString());
					
					bdd = new BinaryDecodedData(bed.encodedString());
					int[] decodedPolynomialArray = bdd.getDecodedPolynomial();
					
					decodedInput.setText(RSEncoder.intArrayToList(decodedPolynomialArray).toString());
				}
			}
		});
		
		decoderButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent evt)
			{
				if( decodedInput.getText().length() > 0 )
				{
					RSDecoder rsDecoder = new RSDecoder(decodedInput.getText());
					boolean decodedArray = rsDecoder.decode();
					if( decodedArray )
					{
						decodedPolynomial.setText("Decoded: " + BinaryDecodedData.fromPolynomialArray(rsDecoder.getDecodedArray()));
					}
					else
					{
						decodedPolynomial.setText("Errors found in positions: " + RSEncoder.intArrayToList(rsDecoder.getErrorPositions()));
					}
				}
			}
		});
		
		Scene scene = new Scene(root, MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
		
		stage.setTitle("DeHawkTrix");
		stage.setScene(scene);
		stage.show();
	}
	
	// wh: width and height of cell
	private void renderCode(GraphicsContext gc, int matrixSize, int wh)
	{
		int xIndex = 100;
		int yIndex = 100;
		int tempX, tempY;
		
		for( int i = 0; i < matrixSize; ++i )
		{
			tempY = ((i + 1) * wh) + yIndex;
			for( int j = 0; j < matrixSize; ++j )
			{
				tempX = ((j + 1) * wh) + xIndex;
				
				if( m1.get(i, j) )
				{
					gc.setFill(Color.BLACK);
					gc.fillRect(tempX, tempY, wh, wh);
				}
				else
				{
					gc.setFill(Color.ALICEBLUE);
					gc.fillRect(tempX, tempY, wh, wh);
				}
			}
		}
	}
}
