import org.junit.Test;

/**
 * Created by Holger on 22.04.2018.
 */
public class HogTest {




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

    @Test
    public void doStart() {
        String imagePath = "C:\\awais_test\\bench.jpg";
        String modelTxtPath = "C:\\development\\workspace\\imagemarker\\src\\main\\resources\\bvlc_googlenet.prototxt";
        String modelBinPath = "C:\\development\\workspace\\imagemarker\\src\\main\\resources\\bvlc_googlenet.caffemodel";
        String classesPath = "C:\\development\\workspace\\imagemarker\\src\\main\\resources\\synset_words.txt";
        //testOpenCVDnnWithCaffeModel(modelTxtPath, modelBinPath, classesPath, imagePath);
    }
/*
    private void testOpenCVDnnWithCaffeModel(String modelTxtPath, String modelBinPath,
                                             String classesFilePath, String imageFilePath) {

        // import the Caffe model
        Net net = readNetFromCaffe(modelTxtPath, modelBinPath);

        // load the class IDs and names
        StringVector classNames = new StringVector();
        readClassNames(classesFilePath, classNames);
        Mat img = imread(imageFilePath);
        if (img.empty())
        {
            throw new RuntimeException("Can't read image from the file: " +imageFilePath);
        }

        resize(img, img, new Size(224, 224)); //GoogLeNet accepts only 224x224 RGB-images
        Mat blob = blobFromImage(img);
        net.setInput(blob); //Convert Mat to dnn::Blob image batch

        // classify the image by applying the blob on the net
        Mat probMat = net.forward();//compute output
        StringVector layerNames = net.getLayerNames();
        for(int i=0; i< layerNames.size(); i++) {
            BytePointer bytePointer = layerNames.get(i);
        }

        System.err.println("img rows: " + img.rows());
        System.err.println("img cols: " + img.cols());

        System.err.println("blob rows: " + blob.rows());
        System.err.println("blob cols: " + blob.cols());

        System.err.println("layerNames.size(): "+layerNames.size());
        System.err.println("Result rows: " + probMat.rows());
        System.err.println("Result cols: " + probMat.cols());

        List<Prob> probs = new ArrayList<>();
        Indexer indexer = probMat.createIndexer();
        for(int i=0; i<probMat.cols(); ++i) {
            double prob = indexer.getDouble(0, i);
            int classId = i;
            probs.add(new Prob(classId, prob));
        }

        Collections.sort(probs, Comparator.comparingDouble(Prob::getProb).reversed());
        for(int i=0; i< 5; i++) {
            Prob prop = probs.get(i);
            String line = "prob: %s   class: %s";

            System.err.println(String.format(line, prop.getProb(), classNames.get(prop.getClassId()).getString()));

        }


        MatShapeVector netInputShapes = new MatShapeVector();
        MatShapeVector outLayerShapes = new MatShapeVector();
        IntPointer netInputShape = new IntPointer(blob);
        int layerID = net.getLayerId("prob");
        //net.getLayerShapes(netInputShape, layerID, netInputShapes, outLayerShapes);

*//*
        net.getBlob

        indexer = probMat.createIndexer();
        for (int i=0; i<5; i++) { // taken from the jupyter notebook
            int x = (int) ((indexer.getDouble(0, i * 2) + 0.5) * 40);
            int y = (int) ((indexer.getDouble(0,i*2+1) + 0.5) * 40);

        }


        System.err.println("flops: "+flops);*//*
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
