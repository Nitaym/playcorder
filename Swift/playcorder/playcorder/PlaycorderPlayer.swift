//
//  Playcorder.swift
//  dronebox
//
//  Created by Edgy Bees on 10/25/17.
//  Copyright © 2017 EdgyBees. All rights reserved.
//

import Foundation

class PlaycorderPlayer {
    static var fileHandle : FileHandle? = FileHandle()
    static var initialized : Bool = false
    static var playing : Bool = false
    
    
    public static func Init(documentName : String) {
        let documentsPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
        let url = URL(fileURLWithPath: documentsPath).appendingPathComponent(documentName)
        Init(fileURL : url)
    }
    public static func Init(filename : String) {
        let url = URL(fileURLWithPath: filename)
        Init(fileURL : url)
    }
    public static func Init(fileURL : URL) {
        do {
            fileHandle = try FileHandle(forReadingFrom: fileURL)
        } catch {
            // File doesn't exist
            return
        }
        
        initialized = true
    }
    
    public static func Play(completion: @escaping (_ packet : Data)->()) {
        playing = true

        DispatchQueue.global(qos: .background).async {
            var packet = ReadPacket()
            while (playing) {
                if (packet == nil) {
                    playing = false
                    return
                }
                
                completion(packet!)
                
                packet = ReadPacket()
            }
        }
    }
    
    public static func Stop() {
        playing = false
    }
    
    public static func ReadPacket() -> Data?{
        if (!initialized) {
            return nil
        }
        
        // Timestamp
        let timeData = fileHandle?.readData(ofLength: 4)
        if (timeData?.count == 0) {
            // EOF reached
            return nil
        }
        
        var timeDiffMS : Int = 0
        timeDiffMS = timeData!.withUnsafeBytes { $0.pointee }
        
        // Size
        let sizeData = fileHandle?.readData(ofLength: 4)
        var packetSize : Int = 0
        packetSize = sizeData!.withUnsafeBytes { $0.pointee }
        
        // Packet
        let packetData = fileHandle?.readData(ofLength: packetSize)

        // Wait for the packet time
        if (timeDiffMS > 0) {
             Thread.sleep(forTimeInterval: Double(timeDiffMS) / 1000);
        }
            
        return packetData;
    }
}



