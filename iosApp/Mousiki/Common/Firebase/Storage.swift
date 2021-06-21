//
//  Storage.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/1/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import FirebaseStorage
import shared

class MousikiAppStorage:  StorageApi {
    
    fileprivate let firebaseStorage = Storage.storage()
    
    func downloadFile(
        remoteUrl: String,
        path: PathComponent,
        connectivityState: ConnectivityChecker,
        logErrorMessage: String,
        completionHandler: @escaping (PathComponent?, Error?) -> Void) {
        
        print("\n\n")
        print("###################")
        print("Call to load file from firebase remote=\(remoteUrl) and to local=\(path.component!)")
        print("###################")
        print("\n\n")
        
        print("Thread is \(Thread.current)")
        let reference = self.firebaseStorage.reference(forURL: remoteUrl)
        
        let fileUrl = URL(string: "file://\(path.component!)")
        reference.write(toFile: fileUrl!) { (url, error) in
            print("Response Thread is \(Thread.current)")
            print("\n\n")
            print("###################")
            print("Got storage response from \(remoteUrl) and error=\(error)")
            print("###################")
            print("\n\n")
            completionHandler(path, nil)
        }
    }
    
}
