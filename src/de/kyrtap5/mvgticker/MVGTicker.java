package de.kyrtap5.mvgticker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class MVGTicker {
    private String station;

    public MVGTicker(String station) {
        this.station = station;
    }

    /**
     * Get an ArrayList with the upcoming departures from the given station
     * @param ubahn Include subway trains in the search
     * @param sbahn Include suburban trains in the search
     * @param bus Include busses in the search
     * @param tram Include tramways in the search
     * @return an ArrayList with the upcoming departures
     */
    public ArrayList<Departure> getDepartures(boolean ubahn, boolean sbahn, boolean bus, boolean tram) {
        ArrayList<Departure> ret = new ArrayList<Departure>();
        try {
            String url = "http://www.mvg-live.de/ims/dfiStaticAuswahl.svc?haltestelle=" + URLEncoder.encode(station, "ISO-8859-1");
            if (ubahn) url += "&ubahn=checked";
            if (sbahn) url += "&sbahn=checked";
            if (tram) url += "&tram=checked";
            if (bus) url += "&bus=checked";

            Document doc = Jsoup.connect(url).get();
            Element content = doc.select("table").get(0);
            Elements rows = content.select("tbody").select("tr");

            for (Element row : rows) {
                if (row.className() == "rowOdd" || row.className() == "rowEven") {
                    Elements cols = row.select("td");
                    ret.add(new Departure(cols.get(0).text(), Transport.getTransportType(cols.get(0).text()),
                            cols.get(1).text(), DateHandler.getDate(Integer.parseInt(cols.get(2).text()))));
                }
            }
            if (sbahn) Collections.sort(ret);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
