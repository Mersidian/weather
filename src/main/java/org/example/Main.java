package org.example;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Weather");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        //Icon Image
        ImageIcon iconImage = new ImageIcon(Main.class.getResource("/images/cloudy.png"));
        frame.setIconImage(iconImage.getImage());

        //Display
        JPanel high = new JPanel();
        high.setLayout(new BoxLayout(high, BoxLayout.Y_AXIS));
        JLabel name = new JLabel();
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        high.add(name);

        JLabel time = new JLabel();
        time.setAlignmentX(Component.CENTER_ALIGNMENT);
        high.add(time);

        System.out.println();

        JLabel temp = new JLabel();
        temp.setAlignmentX(Component.CENTER_ALIGNMENT);
        high.add(temp);

        JLabel icon = new JLabel();
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        high.add(icon);

        //Input Text
        JPanel low = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Enter Location:");
        low.add(label);

        JTextField tf = new JTextField(10); // accepts up to 10 characters
        low.add(tf);

        JButton button = new JButton("Find");
        low.add(button);

        button.addActionListener(_ -> {
            String location = tf.getText();
            HashMap<String, String> weatherData = getData(location);
            //Display Data
            name.setText(weatherData.get("Name") + " " + weatherData.get("Region") + ", "+ weatherData.get("Country"));
            time.setText(weatherData.get("Timezone") + " " + weatherData.get("Localtime"));
            temp.setText(weatherData.get("Temp-C") + "°C");
            try {
                //Display Icon
                URL url = new URL("https:" + weatherData.get("Icon"));
                ImageIcon imageIcon = new ImageIcon(url);
                icon.setIcon(imageIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        frame.getContentPane().add(BorderLayout.SOUTH, low);
        frame.getContentPane().add(BorderLayout.NORTH, high);
        frame.setVisible(true);

    }

    public static HashMap<String, String> getData(String location) {
        Properties props = new Properties();

        //Load API Key
        try (FileInputStream env = new FileInputStream(".env")) {
            props.load(env);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Scanner input = new Scanner(System.in);
//        System.out.print("Type your location: ");
//
//        String location = input.nextLine();

        String api = "http://api.weatherapi.com/v1/current.json?key=" + props.getProperty("API") + "&q=" + location;

        HttpResponse<String> response = null;
        HashMap<String, String> data = null;
        try {
            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Build Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(api))
                    .GET()
                    .build();

            // Send Request
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            Weather weather = objectMapper.readValue(response.body(), Weather.class);

            data = new HashMap<>();
            data.put("Name", weather.getLocation().getName());
            data.put("Region", weather.getLocation().getRegion());
            data.put("Country", weather.getLocation().getCountry());
            data.put("Timezone", weather.getLocation().getTz_id());
            data.put("Localtime", weather.getLocation().getLocaltime());
            data.put("Temp-C", weather.getCurrent().getTemp_c() + "");
            data.put("Text", weather.getCurrent().getCondition().getText());
            data.put("Icon", weather.getCurrent().getCondition().getIcon());

            // Print Response
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}