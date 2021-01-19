package pl.zywickib.crawler.untappd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UntappdResponse {

    private Response response;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Integer total_count;
        private Beers beers;

        @Getter
        @Setter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Beers {
            private List<Item> items;

            @Getter
            @Setter
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Item {
                private Beer beer;
                private Brewery brewery;

                @Getter
                @Setter
                @NoArgsConstructor
                @JsonIgnoreProperties(ignoreUnknown = true)
                static class Beer {
                    private String beer_name;
                    private Double beer_abv;
                    private Integer beer_ibu;
                    private String beer_style;
                    private String beer_description;
                }

                @Getter
                @Setter
                @NoArgsConstructor
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Brewery {
                    private String brewery_name;
                    private String brewery_label;
                }
            }
        }
    }
}
