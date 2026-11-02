package com.worldexplorer.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import com.worldexplorer.net.HttpSupport;

/**
 * Second external service: the Fish API (Marc Conrad, University of Bedfordshire).
 * It returns a picture of fish and the number of fish in it ("base64image,solution").
 * Used for the "Count the Fish" bonus round. If the service is not reachable,
 * getBonusGame() returns null and the game simply carries on without a bonus round.
 */
public class FishServer implements BonusProvider {
    private static final String DEFAULT_URL = "https://marcconrad.com/uob/fish/api.php?out=csv&base64=yes";

    @Override
    public Game getBonusGame() {
        try {
            HttpResponse<String> resp = HttpSupport.getString(Config.get("fish.api.url", DEFAULT_URL), null);
            if (resp.statusCode() != 200) {
                System.out.println("Fish API returned status " + resp.statusCode());
                return null;
            }
            String[] data = resp.body().trim().split(",");
            byte[] png = Base64.getDecoder().decode(data[0].trim());
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(png));
            int solution = Integer.parseInt(data[1].trim());
            if (img == null || solution < 0 || solution > 9) return null;

            List<String> options = new ArrayList<>();
            for (int i = 0; i <= 9; i++) options.add(String.valueOf(i));
            return new Game("How many fish are in the picture?", img, options, solution, true);
        } catch (Exception e) {
            System.out.println("Fish bonus round skipped: " + e);
            return null;
        }
    }
}
