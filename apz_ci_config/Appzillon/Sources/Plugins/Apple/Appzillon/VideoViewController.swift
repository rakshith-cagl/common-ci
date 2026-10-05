//
//  VideoViewController.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 07/06/21.
//
import Foundation
import AVKit
class VideoViewController: UIViewController {
    var videoPath: String?
    let moviePlayer = AVPlayerViewController()
    // MARK: - Init methods
     public init(videoPath: String) {
        self.videoPath = videoPath
        super.init(nibName: nil, bundle: nil)
    }
     required public init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    // MARK: - Implementation
    override open func viewDidLoad() {
        self.playVideoWithSelectedFile(file: self.videoPath ?? StringConstants.Generic.emptyString)
    }
    open override func viewWillDisappear(_ animated: Bool) {
        self.moviePlayer.player?.pause()
        self.moviePlayer.dismiss(animated: true, completion: nil)
    }
    func playVideoWithSelectedFile(file: String) {
        if FileManager.default.fileExists(atPath: file) {
            let videoURL = URL(fileURLWithPath: file)
            let player = AVPlayer(url: videoURL)
            self.moviePlayer.player = player
            NotificationCenter.default.addObserver(self, selector: #selector(playerDidFinishPlaying),
                                                   name: NSNotification.Name.AVPlayerItemDidPlayToEndTime,
                                                   object: moviePlayer.player?.currentItem)
            self.moviePlayer.view.frame = CGRect(x: 0, y: 0,
                                                 width: view.frame.size.width,
                                                 height: view.frame.size.height)
            self.moviePlayer.entersFullScreenWhenPlaybackBegins = true
            self.moviePlayer.videoGravity = .resizeAspect
            self.addChild(self.moviePlayer)
            self.view.addSubview(self.moviePlayer.view)
            player.play()
        }
    }
    func cancelVideoView() {
        self.moviePlayer.dismiss(animated: true)
    }
    @objc func playerDidFinishPlaying(note: NSNotification) {
        self.cancelVideoView()
    }
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    override open func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    open override func willAnimateRotation(to toInterfaceOrientation: UIInterfaceOrientation,
                                           duration: TimeInterval) {
        self.moviePlayer.view.frame = CGRect(x: 0, y: 0, width: view.frame.size.width, height: view.frame.size.height)
        self.moviePlayer.entersFullScreenWhenPlaybackBegins = true
    }
    open override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        self.moviePlayer.view.frame = CGRect(x: 0, y: 0, width: view.frame.size.width, height: view.frame.size.height)
        self.moviePlayer.entersFullScreenWhenPlaybackBegins = true
    }
}
