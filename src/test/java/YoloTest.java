import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Holger on 22.04.2018.
 */
public class YoloTest {


    public static void main(String[] args) throws IOException {

        List<String> collect = Files.list(Paths.get("C:\\development\\dataset\\open_images_lp_train\\img\\positives"))
                .filter(e -> e.getFileName().toString().endsWith(".txt"))
                .map((e) -> {
                    String fName = e.getFileName().toString();
                    int pos = fName.lastIndexOf(".");
                    return fName.substring(0, pos);
                })
                .collect(Collectors.toList());
        System.err.println(String.join("\n", collect));
    }

   /* public static void main(String[] args) {

       *//* opencv_core.Mat img, img_gray = new opencv_core.Mat();
        img = imread("C:\\development\\workspace\\imagemarker\\src\\main\\resources\\sample_images\\car_1_7.png");
        //resizing
        resize(img, img, new opencv_core.Size(64,48)); //Size(64,48) ); //Size(32*2,16*2)); //Size(80,72) );
        //gray
        cvtColor(img, img_gray, opencv_imgproc.COLOR_BGR2GRAY);

        opencv_core.PointVector locations = new opencv_core.PointVector(10000);
        FloatBuffer descriptorsValues = FloatBuffer.allocate(10000);

        //extract feature
        opencv_objdetect.HOGDescriptor hogDescriptor = new opencv_objdetect.HOGDescriptor(new opencv_core.Size(32, 16), new opencv_core.Size(8, 8), new opencv_core.Size(4, 4), new opencv_core.Size(4, 4), 9);
        hogDescriptor.compute( img_gray, descriptorsValues, new opencv_core.Size(0,0), new opencv_core.Size(0,0), locations);

        int row=descriptorsValues.array().length,
        col=descriptorsValues.array().length;

        System.err.println("locations"+ locations.size());
        System.err.println("descriptorsValues"+ descriptorsValues.toString());
        System.err.println("descriptorsValues"+ descriptorsValues.hasArray());
        hogDescriptor.save("C:\\development\\workspace\\imagemarker\\src\\main\\resources\\sample_images\\out\\out.xml");
        opencv_features2d.AKAZE detector = opencv_features2d.AKAZE.create();*//*




    }*/
    /*

    private StringVector getOutputsNames(Net net)
    {
        StringVector names = new StringVector();
        if (names.empty())
        {
            IntPointer outLayers = net.getUnconnectedOutLayers();
            StringVector layerNames = net.getLayerNames();
            names.resize(outLayers.sizeof());

            for (int i = 0; i < outLayers.sizeof(); ++i) {
                String name = layerNames.get(i).getString();
                names.put(i, name);
                System.err.println(name);
            }
            //

            //names[i] = layersNames[outLayers[i] - 1];
        }
        return names;
    }


    public void testReadNet() {
        String modelConfiguration = "C:\\development\\models\\yolo\\yolov2.cfg";
        String modelBinary = "C:\\development\\models\\yolo\\yolov2.weights";


    }


    @Test
    public void testOpenCVDnnWithModel() throws IOException {
        String imageFilePath = "C:\\awais_test\\bench.jpg";
        String modelConfiguration = "C:\\development\\models\\yolo\\yolov2.cfg";
        String modelBinary = "C:\\development\\models\\yolo\\yolov2.weights";

        List<String> classNames = Files.readAllLines(Paths.get("C:\\development\\models\\yolo\\coco.names"));

        // import the Caffe model
        Net net = readNetFromDarknet(modelConfiguration, modelBinary);

        // load the class IDs and names
        Mat img = imread(imageFilePath);
        if (img.empty())
        {
            throw new RuntimeException("Can't read image from the file: " +imageFilePath);
        }
        cvtColor(img, img, COLOR_BGRA2BGR);

        //read in blob
        Mat blob = blobFromImage(img,  1 / 255.F, new Size(416, 416), new Scalar(), true, false);
        net.setInput(blob);

        // classify the image by applying the blob on the net
        Mat detectionMat = net.forward();//compute output
        MatUtil.print(detectionMat);

        System.err.println("img rows: " + img.rows());
        System.err.println("img cols: " + img.cols());

        System.err.println("blob rows: " + blob.rows());
        System.err.println("blob cols: " + blob.cols());

        System.err.println("Result rows: " + detectionMat.rows());
        System.err.println("Result cols: " + detectionMat.cols());


        FloatIndexer indexer = detectionMat.createIndexer();
        double confidenceThreshold = 0.5;
        double maxProb = 0.0;
        Rect rect = null;
        int idx =0;
        for (int i = 0; i < detectionMat.rows(); i++)
        {
            final int probability_index = 4;
            double x = indexer.getDouble(i, 0) * img.cols();
            double y = indexer.getDouble(i, 1) * img.rows();
            double width = indexer.getDouble(i, 2) * img.cols();
            double height = indexer.getDouble(i, 3) * img.rows();
            double prob = indexer.getDouble(i, probability_index);
            int pi = 5;

            if(getMax(detectionMat, i, pi) > 0) {
                System.err.println("prop: "+ prob);
                System.err.println("class: "+ classNames.get(getMaxIndex(detectionMat, i, pi)));
                rect = new Rect((int)x,(int)y,(int)width,(int)height);
                break;
            }
        }

        System.err.println("rect: x:"+rect.x() +" y:"+ rect.y() +" width:"+ rect.width() +"height: "+ rect.height());
    }

    private int getMaxIndex(Mat mat, int row, int col) {
        FloatIndexer indexer = mat.createIndexer();
        float max = 0.0f;

        int idx = -1;
        int maxIndex = 0;
        for(int i = col; i< mat.cols(); i++) {
            idx++;
            if(indexer.get(row, i) > max) {
                max = indexer.get(row, i);
                maxIndex = idx;
            }
        }

        return maxIndex;
    }


    private float getMax(Mat mat, int row, int col) {
        FloatIndexer indexer = mat.createIndexer();
        float max = 0.0f;
        for(int i = col; i< mat.cols(); i++) {
            if(indexer.get(row, i) > max) {
                max = indexer.get(row, i);
            }
        }

        return max;
    }

    void readClassNames(String fileName, StringVector classNames)
    {
        File file = new File(fileName);
        if(!file.exists()) {
            throw new RuntimeException("File with classes labels not found: " + fileName);
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
            for(String line: lines) {
                String substring = line.substring(line.indexOf(" ") + 1);
                classNames.push_back(substring);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
