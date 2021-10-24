//
//  PlayerVC.swift
//  Mousiki
//
//  Created by A.Yabahddou on 5/2/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared
import Kingfisher
import AVKit

class PlayerVC: UIViewController {
    
    @IBOutlet weak var miniPlayerView: UIView!
    @IBOutlet weak var imgTrack: UIImageView!
    @IBOutlet weak var txtTrackTitle: UILabel!
    @IBOutlet weak var txtArtistName: UILabel!
    
    @IBOutlet weak var fullScreenView: UIView!
    @IBOutlet weak var txtElapsedTime: UILabel!
    @IBOutlet weak var txtDuration: UILabel!
    @IBOutlet weak var txtTrackTitleFull: UILabel!
    @IBOutlet weak var txtArtistNameFull: UILabel!
    @IBOutlet weak var durationSlider: UISlider!
    
    
    @IBOutlet weak var songsPager: PlayerSongsPager!
    
    @IBOutlet weak var btnCollapseTopConstraint: NSLayoutConstraint!
    
    //lazy var audioRepository = Injector.au
    lazy var audioRepository: AudioRepository = {
        return Injector().audioRepository
    }()
    
    private var isMinimized = true {
        didSet {
            miniPlayerView.isHidden = !isMinimized
            fullScreenView.isHidden = isMinimized
        }
    }
    
    private var viewTranslation = CGPoint(x: 0, y: 0)
    
    var timeObserverToken: Any?
    
    var currentTrack: DisplayedVideoItem? {
        didSet {
            txtTrackTitle.text = currentTrack?.songTitle
            txtArtistName.text = currentTrack?.artistName()
            txtTrackTitleFull.text = currentTrack?.songTitle
            txtArtistNameFull.text = currentTrack?.artistName()
            if let url = URL(string: currentTrack?.songImagePath ?? "") {
                imgTrack.kf.setImage(with: url)
            }
            
            txtElapsedTime.text = "00:00"
            txtDuration.text = currentTrack?.songDuration
            
            self.loadTrackUrl()
            durationSlider.minimumValue = 0.0
            if let duration = currentTrack?.track.durationToSeconds() {
                durationSlider.maximumValue = Float(duration)
            }
        }
    }
    
    private var queue: [DisplayedVideoItem] = []
    
    var player:AVPlayer?
    var playerItem:AVPlayerItem?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        imgTrack.image = nil
        fullScreenView.isHidden = true
        imgTrack.setCorner(radius: 4)
        view.addGestureRecognizer(UIPanGestureRecognizer(target: self, action: #selector(handleDismiss)))
        if #available(iOS 13.0, *) {
            let window = UIApplication.shared.windows[0]
            let topPadding = window.safeAreaInsets.top
            btnCollapseTopConstraint.constant = topPadding
        }
        self.songsPager.pagerDelegate = self
    }
    
    func setCurrentSong(_ song: DisplayedVideoItem, queue: [DisplayedVideoItem]) {
        self.currentTrack = song
        self.songsPager.songs = queue
    }
    
    @IBAction func didTapExpand(_ sender: Any) {
        expand()
    }
    
    @IBAction func didTapCollapse(_ sender: Any) {
        collapse()
    }
    
    @IBAction func didTapShowPlaylist(_ sender: Any) {
        
    }
    
    func collapse() {
        let tabBar = self.tabBarController as? TabBarController
        tabBar?.minimizePlayer()
        self.isMinimized = true
    }
    
    func expand() {
        let tabBar = self.tabBarController as? TabBarController
        tabBar?.maximizePlayer()
        self.isMinimized = false
        
        // Scroll to current
        let queue = self.songsPager.songs
        guard let song = self.currentTrack else { return }
        if let index = queue.firstIndex(of: song) {
            let indexPath = IndexPath(item: index, section: 0)
            self.songsPager.scrollToItem(at: indexPath, at: [.centeredVertically, .centeredHorizontally], animated: false)
            //self.songsPager.currentIndex = index
            print("should scroll to song at index \(index)")
        }
    }
    
    @objc
    private func handleDismiss(sender: UIPanGestureRecognizer) {
        guard !isMinimized else { return }
        viewTranslation = sender.translation(in: view)
        
        switch sender.state {
        case .changed:
            UIView.animate(withDuration: 0.5,
                           delay: 0,
                           usingSpringWithDamping: 0.7,
                           initialSpringVelocity: 1,
                           options: .curveEaseOut,
                           animations: {
                            self.view.transform = CGAffineTransform(translationX: 0, y: self.viewTranslation.y)
                           })
        case .ended:
            if viewTranslation.y < 200 {
                UIView.animate(withDuration: 0.5,
                               delay: 0,
                               usingSpringWithDamping: 0.7,
                               initialSpringVelocity: 1,
                               options: .curveEaseOut,
                               animations: {
                                self.view.transform = .identity
                               })
            } else {
                UIView.animate(withDuration: 0.5,
                               delay: 0,
                               usingSpringWithDamping: 0.7,
                               initialSpringVelocity: 1,
                               options: .curveEaseOut,
                               animations: {
                                self.view.transform = .identity
                                self.collapse()
                                self.isMinimized = true
                               })
                
            }
        default:
            break
        }
    }
    
    
    private func playAudio(streamUrl: String) {
        if let url = URL(string: streamUrl) {
            let playerItem:AVPlayerItem = AVPlayerItem(url: url)
            if player == nil {
                player = AVPlayer(playerItem: playerItem)
                let playerLayer = AVPlayerLayer(player: player!)
                playerLayer.frame = CGRect(x:0, y:0, width:10, height:50)
                self.view.layer.addSublayer(playerLayer)
                self.addPeriodicTimeObserver()
            } else {
                let playerItem:AVPlayerItem = AVPlayerItem(url: url)
                player?.replaceCurrentItem(with: playerItem)
            }
            player?.play()
        }
    }
    
    private func loadTrackUrl() {
        guard let videoId = (self.currentTrack?.track as? YtbTrack)?.id else {
            return
        }
        
        audioRepository.getAudioUrlLocal(videoId: videoId) { (url, error) in
            if let url = url {
                print("Streaming url: \(url)")
                self.playAudio(streamUrl: url)
            } else {
                print("Got no straming url")
            }
        }
    }
    
    @IBAction func goToPreviousSong(_ sender: Any) {
        let queue = self.songsPager.songs
        guard let song = self.currentTrack else { return }
        if let index = queue.firstIndex(of: song) {
            if index == 0 { return }
            self.goToSongAt(index - 1)
        }
    }
    
    @IBAction func goToNextSong(_ sender: Any) {
        let queue = self.songsPager.songs
        guard let song = self.currentTrack else { return }
        if let index = queue.firstIndex(of: song) {
            if index == queue.count - 1 { return }
            self.goToSongAt(index + 1)
        }
    }
    
    
    @IBAction func playPause(_ sender: Any) {
        if player?.status != .readyToPlay { return }
        if player!.timeControlStatus == .playing {
            player?.pause()
        } else {
            player?.play()
        }
    }
    
    
    @IBAction func durationSliderProgress(_ sender: UISlider) {
        if player?.status != .readyToPlay { return }
        let seconds = sender.value
        let time = CMTime(value: CMTimeValue(seconds), timescale: 1)
        player?.seek(to: time)
    }
    
    private func goToSongAt(_ index: Int) {
        let indexPath = IndexPath(item: index, section: 0)
        self.songsPager.scrollToItem(at: indexPath, at: [.centeredVertically, .centeredHorizontally], animated: true)
        self.currentTrack = queue[index]
    }
    
    private func addPeriodicTimeObserver() {
        // Notify every half second
        let timeScale = CMTimeScale(NSEC_PER_SEC)
        let time = CMTime(seconds: 1, preferredTimescale: timeScale)
        
        timeObserverToken = player?.addPeriodicTimeObserver(forInterval: time, queue: .main) { [weak self] time in
            
            let currentSeconds = Int(CMTimeGetSeconds(time))
            if let duration = self?.player?.currentItem?.asset.duration {
                let trackDuration = Int(CMTimeGetSeconds(duration))
                print("trackDuration=\(trackDuration) and current=\(currentSeconds)")
                if trackDuration == currentSeconds {
                    print("End track")
                } else {
                    self?.durationSlider.value = Float(time.seconds)
                    self?.updateCurrentTrackTime(elapsedSeconds: Int(time.seconds))
                }
            }
        }
    }
    
    private func removePeriodicTimeObserver() {
        if let timeObserverToken = timeObserverToken {
            player?.removeTimeObserver(timeObserverToken)
            self.timeObserverToken = nil
        }
    }
    
    private func updateCurrentTrackTime(elapsedSeconds: Int) {
        let minutes = elapsedSeconds / 60
        let seconds = elapsedSeconds % 60
        self.txtElapsedTime.text = String(format: "%d:%02d",  minutes, seconds)
    }
}


extension PlayerVC: PlayerSongsPagerDelegate {
    func didScrollToSong(_ song: DisplayedVideoItem) {
        self.currentTrack = song
    }
}
