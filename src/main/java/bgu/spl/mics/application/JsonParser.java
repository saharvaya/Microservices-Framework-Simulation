/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * JsonParser class purpose is to parse given JSON format information given
 * and deserialize it using Gson into a ParsedInformation Object.
 */
class JsonParser {

    private Gson gson; // Gson object used to parse json file

    //Constructor
    JsonParser() {
        this.gson = new Gson();
    }
    
    ParsedInformation parse(String jsonFilePath) throws FileNotFoundException
    {
        BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
        return gson.fromJson(reader, ParsedInformation.class); // Parse JSON file information to ParsedInformation object
    }
    
}