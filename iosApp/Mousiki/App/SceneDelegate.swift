//
//  SceneDelegate.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/19/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    
    var window: UIWindow?
    var tabBarController: UITabBarController?
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
        guard let windowScene = (scene as? UIWindowScene) else { return }
        window = UIWindow(windowScene: windowScene)
        window?.overrideUserInterfaceStyle = .dark
        (UIApplication.shared.delegate as? AppDelegate)?.self.window = window
        showDashboard()
    }
    
    func sceneDidDisconnect(_ scene: UIScene) {
        // Called as the scene is being released by the system.
        // This occurs shortly after the scene enters the background, or when its session is discarded.
        // Release any resources associated with this scene that can be re-created the next time the scene connects.
        // The scene may re-connect later, as its session was not neccessarily discarded (see `application:didDiscardSceneSessions` instead).
    }
    
    func sceneDidBecomeActive(_ scene: UIScene) {
        // Called when the scene has moved from an inactive state to an active state.
        // Use this method to restart any tasks that were paused (or not yet started) when the scene was inactive.
    }
    
    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will move from an active state to an inactive state.
        // This may occur due to temporary interruptions (ex. an incoming phone call).
    }
    
    func sceneWillEnterForeground(_ scene: UIScene) {
        // Called as the scene transitions from the background to the foreground.
        // Use this method to undo the changes made on entering the background.
    }
    
    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
        // Use this method to save data, release shared resources, and store enough scene-specific state information
        // to restore the scene back to its current state.
    }
    
    
    
    func showDashboard() {
        tabBarController = UITabBarController()
        
        // Customize UITabBarItem appearance
        UITabBarItem.appearance().setTitleTextAttributes([NSAttributedString.Key.font: robotoRegular(size: 14)!], for: .normal)
        UITabBarItem.appearance().setTitleTextAttributes([NSAttributedString.Key.font: robotoMedium(size: 14)!], for: .selected)
        UITabBarItem.appearance().setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], for: .selected)
        
        // Create viewControllers
        let homeVC = HomeVC.loadFromNib()
        
        homeVC.tabBarItem = UITabBarItem(title: "menu_home".localized,
                                         image: R.image.baseline_home_white_24pt(),
                                         tag: 0)
        
        let libraryVC = LibraryVC.loadFromNib()
        libraryVC.tabBarItem = UITabBarItem(title: "menu_library".localized,
                                            image: R.image.baseline_library_music_white_24pt(),
                                            tag: 1)
        
        let mainSearchVC = MainSearchVC.loadFromNib()
        mainSearchVC.tabBarItem = UITabBarItem(title: "menu_search".localized,
                                               image: R.image.baseline_search_white_24pt(),
                                               tag: 2)
        
        tabBarController?.viewControllers = [homeVC, libraryVC, mainSearchVC].map {
            UINavigationController(rootViewController: $0)
        }
        
        setRootViewController(tabBarController!)
    }
    
    private func setRootViewController(_ vc: UIViewController) {
        window?.rootViewController = vc
        window?.makeKeyAndVisible()
        if let window = window {
            UIView.transition(with: window,
                              duration: 0.3,
                              options: .transitionCrossDissolve,
                              animations: nil,
                              completion: nil)
        }
    }
}

