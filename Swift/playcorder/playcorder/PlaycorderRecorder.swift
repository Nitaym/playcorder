//
//  Playcorder.swift
//  dronebox
//
//  Created by Edgy Bees on 10/25/17.
//  Copyright © 2017 EdgyBees. All rights reserved.
//

import Foundation

class PlaycorderRecorder {
    static var filename : String = ""
    static var fileURL : URL = URL(fileURLWithPath: "")
    static var fileHandle : FileHandle? = FileHandle()
    
    static var lastPacketTime : Date = Date(timeIntervalSinceNow: 0)
    
    static var enabled : Bool = false
    
    public static func StartRecording() {
        enabled = true
        
        let dateObj = Date.init(timeIntervalSinceNow: 0)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMdd-hhmmss"
        filename = dateFormatter.string(from: dateObj) + ".packets"

        let documentsPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
        fileURL = URL(fileURLWithPath: documentsPath).appendingPathComponent(filename)
        
        do {
            fileHandle = try FileHandle(forWritingTo: fileURL)
        } catch {
            // File doesn't exist. Create
            let data = Data()
            try? data.write(to: fileURL, options: Data.WritingOptions.atomic)
            
            // Get new filehandle
            fileHandle = try! (FileHandle(forWritingTo: fileURL))
        }
    }
    
    public static func StopRecording() {
        enabled = false
        fileHandle?.closeFile()
    }
    
    public static func SavePacket(buffer : Data) {
        if (!enabled) {
            return
        }
        
        if fileHandle == nil {
            StartRecording()

            if fileHandle == nil {
                return
            }
        }

        var packetData : Data = Data()

        // Timestamp
        let now = Date()
        let elapsed = now.timeIntervalSince(lastPacketTime)
        var elapsedMS : UInt32 = UInt32(elapsed * 1000)
        packetData.append(Data(bytes : &elapsedMS, count: MemoryLayout.size(ofValue: elapsedMS)))

        // Size
        var packetSize : UInt32 = UInt32(buffer.count)
        packetData.append(Data(bytes: &packetSize, count: MemoryLayout.size(ofValue: packetSize)))

        // Packet
        packetData.append(buffer)

        fileHandle?.write(packetData)
//        try? packetData.w(to: fileURL, options: Data.WritingOptions.)(to: fileURL)
        
        lastPacketTime = Date()
    }
}


