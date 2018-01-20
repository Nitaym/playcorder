//
//  main.swift
//  playcorder
//
//  Created by Edgy Bees on 1/19/18.
//  Copyright © 2018 Edgy Bees. All rights reserved.
//

import Foundation

//PlaycorderRecorder.StartRecording()
//PlaycorderRecorder.SavePacket(buffer: "Message1".data(using: String.Encoding.ascii)!)
//print("Message1")
//PlaycorderRecorder.SavePacket(buffer: "Message2".data(using: String.Encoding.ascii)!)
//print("Message2")
//usleep(2000)
//PlaycorderRecorder.SavePacket(buffer: "Message3".data(using: String.Encoding.ascii)!)
//print("Message3")
//usleep(500000)
//PlaycorderRecorder.SavePacket(buffer: "Message4".data(using: String.Encoding.ascii)!)
//print("Message4")

let filename = "20180119-182515.packets"
PlaycorderPlayer.Init(documentName : filename)

func printMessage(data : Data) {
    let s = String.init(data: data, encoding: String.Encoding.ascii)
    print(s!)
}
PlaycorderPlayer.Play(completion: printMessage)
usleep(2000000)
print("Hello, World!")

