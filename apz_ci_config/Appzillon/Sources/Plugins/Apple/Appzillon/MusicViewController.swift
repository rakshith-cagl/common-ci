// Copyright (c) 2021 Appzillon. All rights reserved.

import UIKit
import MediaPlayer

class MusicViewController: UIViewController {
    let musicPlayer: MPMusicPlayerController = MPMusicPlayerController.systemMusicPlayer
    var playPauseButton = UIButton()
    var songSlider = UISlider()
    var mediaItemCollection: MPMediaItemCollection?
    var albumImageView = UIImageView()
    var albumTitleLabel = UILabel()
    var albumArtistLabel = UILabel()
    var albumNameLabel = UILabel()
    var sliderMinValueLabel = UILabel()
    var sliderMaxValueLabel = UILabel()
    var nextSongButton = UIButton()
    var previousSongButton = UIButton()
    var musicDelegate: MusicDelegate?
    let pauseIcon = "pauseIcon.png"
    let playIcon = "playIcon.png"

    override func viewDidLoad() {
        super.viewDidLoad()
        getViewToBeDisplayed()
        if musicPlayer.playbackState == .playing {
            playPauseButton.setImage(UIImage(named: pauseIcon), for: .normal)
        } else {
            playPauseButton.setImage(UIImage(named: playIcon), for: .normal)
        }
        songSlider.value = 0
        self.title = "Song"
        self.navigationItem.leftBarButtonItem = UIBarButtonItem.init(barButtonSystemItem: .cancel,
                                                                     target: self,
                                                                     action: #selector(cancelVideoView))
        self.view.backgroundColor = .white
        if let mediaItemCollection = mediaItemCollection {
            musicPlayer.setQueue(with: mediaItemCollection)
            musicPlayer.prepareToPlay()
            registerMediaPlayerNotifications()
            if let duration = musicPlayer.nowPlayingItem?.value(forProperty: MPMediaItemPropertyPlaybackDuration)
                as? TimeInterval {
                songSlider.maximumValue = Float(duration)
                songSlider.value = 00.00
                sliderMaxValueLabel.text = convertSecondsToReadableTime(receviedSeconds: Float(duration))
                Timer.scheduledTimer(timeInterval: 1.0,
                                     target: self,
                                     selector: #selector(updateTime),
                                     userInfo: nil,
                                     repeats: true)
                musicPlayer.play()
                playPauseButton.setImage(UIImage(named: pauseIcon), for: .normal)
                getSongInfo()
            }
        }
    }
    @objc func cancelVideoView() {
        let notificationCenter = NotificationCenter.default
        notificationCenter.removeObserver(self,
                                          name: .MPMusicPlayerControllerNowPlayingItemDidChange,
                                          object: musicPlayer)
        notificationCenter.removeObserver(self,
                                          name: .MPMusicPlayerControllerPlaybackStateDidChange,
                                          object: musicPlayer)
        notificationCenter.removeObserver(self,
                                          name: .MPMusicPlayerControllerVolumeDidChange,
                                          object: musicPlayer)
        musicPlayer.endGeneratingPlaybackNotifications()
        musicPlayer.stop()
        dismiss(animated: true) {
            APZLogger.log(logLvl: "I", message: "AudioViewController Cancelled")
            self.musicDelegate?.cancelMusicView(interface: WindowUtility.getUiInterfaceOrientation())
        }
    }
    @objc func updateTime() {
        songSlider.value = Float(musicPlayer.currentPlaybackTime)
        sliderMinValueLabel.text = convertSecondsToReadableTime(receviedSeconds: Float(musicPlayer.currentPlaybackTime))
    }
    func registerMediaPlayerNotifications() {
        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self,
                                       selector: #selector(handleNowPlayingItemChanged),
                                       name: .MPMusicPlayerControllerNowPlayingItemDidChange,
                                       object: musicPlayer)
        notificationCenter.addObserver(self,
                                       selector: #selector(handlePlaybackStateChanged),
                                       name: .MPMusicPlayerControllerNowPlayingItemDidChange,
                                       object: musicPlayer)
        musicPlayer.beginGeneratingPlaybackNotifications()
    }
    @objc func handleNowPlayingItemChanged() {
        getSongInfo()
    }
    @objc func handlePlaybackStateChanged() {
        let playBackState: MPMusicPlaybackState = musicPlayer.playbackState
        if playBackState == .paused {
            playPauseButton.setImage(UIImage(named: playIcon), for: .normal)
        } else if playBackState == .playing {
            playPauseButton.setImage(UIImage(named: pauseIcon), for: .normal)
        } else if playBackState == .stopped {
            playPauseButton.setImage(UIImage(named: playIcon), for: .normal)
            musicPlayer.stop()
        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @objc func songSliderAction() {
        musicPlayer.currentPlaybackTime = TimeInterval(songSlider.value)
    }
    @objc func playPauseAction() {
        if musicPlayer.playbackState == .playing {
            musicPlayer.pause()
            playPauseButton.setImage(UIImage(named: playIcon), for: .normal)
        } else {
            musicPlayer.play()
            playPauseButton.setImage(UIImage(named: pauseIcon), for: .normal)
        }
    }
    @objc func previousSong() {
        //Do nothing
    }
    @objc func nextSong() {
        //Do nothing
    }
    func convertSecondsToReadableTime(receviedSeconds: Float) -> String {
        let currentSeconds: Float64 = Float64(receviedSeconds)
        let mins: Int = Int(currentSeconds/60.0)
        let secs: Int = Int(fmodf(Float(currentSeconds), 60.0))
        let minsString: String = mins < 10 ? String(format: "0%d", mins) : String(format: "%d", mins)
        let secsString: String = secs < 10 ? String(format: "0%d", secs) : String(format: "%d", secs)
        let timeString = String(format: "%@:%@", minsString, secsString)
        return timeString
    }
    func getSongInfo() {
        if let currentItem: MPMediaItem = musicPlayer.nowPlayingItem {
            if let _ = UIImage(named: "noPhoto.png"),
               let artWork: MPMediaItemArtwork = currentItem.value(forProperty: MPMediaItemPropertyArtwork)
                as? MPMediaItemArtwork {
                albumImageView.image = artWork.image(at: CGSize(width: 240, height: 240))!
            }
            if let titileString = currentItem.value(forProperty: MPMediaItemPropertyTitle) as? String {
                albumTitleLabel.text = String(format: "Title: %@", titileString)
            } else {
                albumTitleLabel.text = "Title: Unknown title"
            }
            if let artistString = currentItem.value(forProperty: MPMediaItemPropertyArtist) as? String {
                albumArtistLabel.text = String(format: "Artist: %@", artistString)
            } else {
                albumArtistLabel.text = "Artist: Unknown artist"
            }
            if let albumString = currentItem.value(forProperty: MPMediaItemPropertyAlbumTitle) as? String,
               albumString != "" {
                albumNameLabel.text = String(format: "Album: %@", albumString)
            } else {
                albumNameLabel.text = "Album: Unknown album"
            }
        }
    }
    func getViewToBeDisplayed() {
        sliderMinValueLabel = UILabel.init(frame: CGRect(x: 20,
                                                         y: self.view.frame.size.height - 20 - 30,
                                                         width: 50, height: 30))
        sliderMinValueLabel.text = "00.00"
        sliderMinValueLabel.textAlignment = .center
        self.view.addSubview(sliderMinValueLabel)
        songSlider = UISlider.init(frame: CGRect(x: self.sliderMinValueLabel.frame.origin.x +
                                                 sliderMinValueLabel.frame.size.width + 10,
                                                 y: self.view.frame.size.height - 20 - 30,
                                                 width: self.view.frame.size.width -
                                                 sliderMinValueLabel.frame.origin.x -
                                                 sliderMinValueLabel.frame.size.width -
                                                 10 - sliderMinValueLabel.frame.origin.x -
                                                 sliderMinValueLabel.frame.size.width - 10,
                                                 height: 30))
        songSlider.addTarget(self, action: #selector(songSliderAction), for: .valueChanged)
        songSlider.isContinuous = true
        self.view.addSubview(songSlider)
        sliderMaxValueLabel = UILabel.init(frame: CGRect(x: songSlider.frame.origin.x +
                                                         songSlider.frame.size.width + 10,
                                                        y: self.view.frame.size.height - 20 - 30,
                                                        width: 50,
                                                        height: 30))
        sliderMaxValueLabel.text = "00.00"
        sliderMaxValueLabel.textAlignment = .center
        self.view.addSubview(sliderMaxValueLabel)
        if UIDevice.current.userInterfaceIdiom == .phone {
            // To check the device is in landscape mode
            if self.view.frame.size.height < self.view.frame.size.width {
                nextSongButton = UIButton.init(frame: CGRect(x: self.view.frame.size.width -
                                                             20 - 40,
                                                             y: 65, width: 40,
                                                             height: 40))
                playPauseButton = UIButton.init(frame: CGRect(x: nextSongButton.frame.origin.x -
                                                              20 - 80,
                                                              y: 40, width: 80,
                                                              height: 80))
                previousSongButton = UIButton.init(frame: CGRect(x: playPauseButton.frame.origin.x -
                                                                 20 - 40,
                                                                 y: 65, width: 40,
                                                                 height: 40))
                albumImageView = UIImageView.init(frame: CGRect(x: 20,
                                                                y: 40,
                                                                width: 180,
                                                                height: 120))
                albumTitleLabel = UILabel.init(frame: CGRect(x: 20,
                                                             y: albumImageView.frame.origin.y +
                                                             albumImageView.frame.size.height +
                                                             20,
                                                             width: self.view.frame.size.width -
                                                             40,
                                                             height: 24))
                albumArtistLabel = UILabel.init(frame: CGRect(x: 20,
                                                              y: albumTitleLabel.frame.origin.y +
                                                              albumTitleLabel.frame.size.height +
                                                              20,
                                                              width: self.view.frame.width - 40,
                                                              height: 24))
                albumNameLabel = UILabel.init(frame: CGRect(x: 20,
                                                            y: albumArtistLabel.frame.origin.y +
                                                            albumArtistLabel.frame.size.height +
                                                            20,
                                                            width: self.view.frame.size.width - 40,
                                                            height: 24))
            } else {
                playPauseButton = UIButton.init(frame: CGRect(x: 0,
                                                              y: songSlider.frame.origin.y - 100,
                                                              width: 80,
                                                              height: 80))
                playPauseButton.center = CGPoint(x: self.view.bounds.midX,
                                                 y: playPauseButton.center.y)
                previousSongButton = UIButton.init(frame: CGRect(x: playPauseButton.frame.origin.x -
                                                                 20 - 40,
                                                                 y: songSlider.frame.origin.y -
                                                                 100 + 25,
                                                                 width: 40, height: 40))
                nextSongButton = UIButton.init(frame: CGRect(x: playPauseButton.frame.origin.x +
                                                             playPauseButton.frame.size.width + 20,
                                                             y: songSlider.frame.origin.y - 100 + 25,
                                                             width: 40, height: 40))
                albumImageView = UIImageView.init(frame: CGRect(x: 20, y: 85, width: 150, height: 150))
                albumImageView.center = CGPoint(x: self.view.bounds.midX, y: albumImageView.center.y)
                albumTitleLabel = UILabel.init(frame: CGRect(x: 20,
                                                             y: albumImageView.frame.origin.y +
                                                             albumImageView.frame.size.height + 20,
                                                             width: self.view.frame.size.width - 40,
                                                             height: 24))
                albumArtistLabel = UILabel.init(frame: CGRect(x: 20,
                                                              y: albumTitleLabel.frame.origin.y +
                                                              albumTitleLabel.frame.size.height + 20,
                                                              width: self.view.frame.size.width - 40,
                                                              height: 24))
                albumNameLabel = UILabel.init(frame: CGRect(x: 20,
                                                            y: albumArtistLabel.frame.origin.y +
                                                            albumArtistLabel.frame.size.height + 20,
                                                            width: self.view.frame.size.width - 40,
                                                            height: 24))
            }
        } else {
            playPauseButton = UIButton.init(frame: CGRect(x: 0,
                                                          y: songSlider.frame.origin.y - 120,
                                                          width: 100, height: 100))
            playPauseButton.center = CGPoint(x: self.view.bounds.midX, y: playPauseButton.center.y)
            previousSongButton = UIButton.init(frame: CGRect(x: playPauseButton.frame.origin.x -
                                                             20 - 50,
                                                             y: songSlider.frame.origin.y -
                                                             120 + 25,
                                                             width: 50, height: 50))
            nextSongButton = UIButton.init(frame: CGRect(x: playPauseButton.frame.origin.x +
                                                         playPauseButton.frame.size.width + 20,
                                                         y: songSlider.frame.origin.y - 120 + 25,
                                                         width: 50, height: 50))
            albumImageView = UIImageView.init(frame: CGRect(x: 20, y: 85, width: 250, height: 250))
            albumImageView.center = CGPoint(x: self.view.bounds.midX, y: albumImageView.center.y)
            albumTitleLabel = UILabel.init(frame: CGRect(x: 20,
                                                         y: albumImageView.frame.origin.y +
                                                         albumImageView.frame.size.height + 20,
                                                         width: self.view.frame.size.width - 40, height: 24))
            albumArtistLabel = UILabel.init(frame: CGRect(x: 20,
                                                          y: albumTitleLabel.frame.origin.y +
                                                          albumTitleLabel.frame.size.height + 20,
                                                          width: self.view.frame.size.width - 40,
                                                          height: 24))
            albumNameLabel = UILabel.init(frame: CGRect(x: 20,
                                                        y: albumArtistLabel.frame.origin.y +
                                                        albumArtistLabel.frame.size.height + 20,
                                                        width: self.view.frame.size.width - 40,
                                                        height: 24))
        }
        playPauseButton.addTarget(self, action: #selector(playPauseAction), for: .touchUpInside)
        playPauseButton.setImage(UIImage(named: playIcon), for: .normal)
        self.view.addSubview(playPauseButton)
        previousSongButton.addTarget(self, action: #selector(previousSong), for: .touchUpInside)
        previousSongButton.setImage(UIImage(named: "previousSong.png"), for: .normal)
        self.view.addSubview(previousSongButton)
        nextSongButton.addTarget(self, action: #selector(nextSong), for: .touchUpInside)
        nextSongButton.setImage(UIImage(named: "nextSong.png"), for: .normal)
        self.view.addSubview(nextSongButton)
        albumImageView.image = UIImage(named: "noPhoto.png")
        self.view.addSubview(albumImageView)
        albumTitleLabel.text = "Title: Unknown title"
        albumTitleLabel.textAlignment = .center
        self.view.addSubview(albumTitleLabel)
        albumArtistLabel.text = "Artist: Unknown artist"
        albumArtistLabel.textAlignment = .center
        self.view.addSubview(albumArtistLabel)
        albumNameLabel.text = "Album: Unknown album"
        albumNameLabel.textAlignment = .center
        self.view.addSubview(albumNameLabel)
    }
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        // To check the device is in landscape mode or not and to display the view accordingly
        if size.width > self.view.frame.size.width {
            let displayRect = CGRect(x: 0, y: 0, width: size.width, height: size.height)
            sliderMinValueLabel.frame = CGRect(x: 20, y: size.height - 20 - 30, width: 50, height: 30)
            songSlider.frame = CGRect(x: sliderMinValueLabel.frame.origin.x + sliderMinValueLabel.frame.size.width + 10,
                                      y: size.height - 20 - 30, width: size.width - sliderMinValueLabel.frame.origin.x
                                      - sliderMinValueLabel.frame.size.width - 10 - sliderMinValueLabel.frame.origin.x
                                      - sliderMinValueLabel.frame.size.width - 10,
                                      height: 30)
            sliderMaxValueLabel.frame = CGRect(x: songSlider.frame.origin.x + songSlider.frame.size.width + 10,
                                               y: size.height - 20 - 30,
                                               width: 50, height: 30)
            if UIDevice.current.userInterfaceIdiom == .phone {
                nextSongButton.frame = CGRect(x: size.width - 20 - 40, y: 65, width: 40, height: 40)
                playPauseButton.frame = CGRect(x: nextSongButton.frame.origin.x - 20 - 80,
                                               y: 40, width: 80, height: 80)
                previousSongButton.frame = CGRect(x: playPauseButton.frame.origin.x - 20 - 40,
                                                  y: 65, width: 40, height: 40)
                albumImageView.frame = CGRect(x: 20, y: 40, width: 180, height: 120)
            } else {
                playPauseButton.frame = CGRect(x: 0,
                                               y: songSlider.frame.origin.y - 120,
                                               width: 100, height: 100)
                playPauseButton.center = CGPoint(x: displayRect.midX, y: playPauseButton.center.y)
                previousSongButton.frame = CGRect(x: playPauseButton.frame.origin.x - 20 - 50,
                                                  y: songSlider.frame.origin.y - 120 + 25,
                                                  width: 50, height: 50)
                nextSongButton.frame = CGRect(x: playPauseButton.frame.origin.x + playPauseButton.frame.size.width + 20,
                                              y: songSlider.frame.origin.y - 120 + 25,
                                              width: 50, height: 50)
                albumImageView.frame = CGRect(x: 20, y: 85, width: 250, height: 250)
                albumImageView.center = CGPoint(x: displayRect.midX, y: albumImageView.center.y)
            }
            albumTitleLabel.frame = CGRect(x: 20,
                                           y: albumImageView.frame.origin.y + albumImageView.frame.size.height + 20,
                                           width: size.width - 40, height: 24)
            albumArtistLabel.frame = CGRect(x: 20,
                                            y: albumTitleLabel.frame.origin.y + albumTitleLabel.frame.size.height + 20,
                                            width: size.width - 40, height: 24)
            albumNameLabel.frame = CGRect(x: 20,
                                          y: albumArtistLabel.frame.origin.y + albumArtistLabel.frame.size.height + 20,
                                          width: size.width - 40, height: 24)
        } else {
            let displayRect = CGRect(x: 0, y: 0, width: size.width, height: size.height)
            sliderMinValueLabel.frame = CGRect(x: 20, y: size.height - 20 - 30, width: 50, height: 30)
            songSlider.frame = CGRect(x: sliderMinValueLabel.frame.origin.x + sliderMinValueLabel.frame.size.width + 10,
                                      y: size.height - 20 - 30, width: size.width - sliderMinValueLabel.frame.origin.x
                                      - sliderMinValueLabel.frame.size.width - 10 - sliderMinValueLabel.frame.origin.x
                                      - sliderMinValueLabel.frame.size.width - 10,
                                      height: 30)
            sliderMaxValueLabel.frame = CGRect(x: songSlider.frame.origin.x + songSlider.frame.size.width + 10,
                                               y: size.height - 20 - 30,
                                               width: 50, height: 30)
            if UIDevice.current.userInterfaceIdiom == .phone {
                playPauseButton.frame = CGRect(x: 0, y: songSlider.frame.origin.y - 100, width: 80, height: 80)
                playPauseButton.center = CGPoint(x: displayRect.midX, y: playPauseButton.center.y)
                previousSongButton.frame = CGRect(x: playPauseButton.frame.origin.x - 20 - 40,
                                                  y: songSlider.frame.origin.y - 100 + 25,
                                                  width: 40, height: 40)
                nextSongButton.frame = CGRect(x: playPauseButton.frame.origin.x +
                                              playPauseButton.frame.size.width + 20,
                                              y: songSlider.frame.origin.y - 100 + 25,
                                              width: 40, height: 40)
                albumImageView.frame = CGRect(x: 20, y: 85, width: 150, height: 150)
            } else {
                playPauseButton.frame = CGRect(x: 0, y: songSlider.frame.origin.y - 120, width: 100, height: 100)
                playPauseButton.center = CGPoint(x: displayRect.midX, y: playPauseButton.center.y)
                previousSongButton.frame = CGRect(x: playPauseButton.frame.origin.x - 20 - 50,
                                                  y: songSlider.frame.origin.y - 120 + 25,
                                                  width: 50, height: 50)
                nextSongButton.frame = CGRect(x: playPauseButton.frame.origin.x + playPauseButton.frame.size.width + 20,
                                              y: songSlider.frame.origin.y - 120 + 25,
                                              width: 50, height: 50)
                albumImageView.frame = CGRect(x: 20, y: 85, width: 250, height: 250)
            }
            albumImageView.center = CGPoint(x: displayRect.midX, y: albumImageView.center.y)
            albumTitleLabel.frame = CGRect(x: 20,
                                           y: albumImageView.frame.origin.y +
                                           albumImageView.frame.size.height + 20,
                                           width: size.width - 40, height: 24)
            albumArtistLabel.frame = CGRect(x: 20,
                                            y: albumTitleLabel.frame.origin.y +
                                            albumTitleLabel.frame.size.height + 20,
                                            width: size.width - 40, height: 24)
            albumNameLabel.frame = CGRect(x: 20,
                                          y: albumArtistLabel.frame.origin.y +
                                          albumArtistLabel.frame.size.height + 20,
                                          width: size.width - 40, height: 24)
        }
    }
}
