package edu.upf;

import java.util.List;

import edu.upf.model.HashTagCount;
import edu.upf.storage.DynamoHashTagRepository;

public class MastodonHashtagsReader {

        public static void main(String[] args) throws InterruptedException {
            String lan = args[0];

            DynamoHashTagRepository repository = new DynamoHashTagRepository();

            List<HashTagCount> top10 = repository.readTop10(lan);

            for(HashTagCount h: top10){
                System.out.println("(#" + h.getHashtag() + " , " + h.getCount() + ")");
            }
        }
}