import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Room} from '../../model/room';
import {GameService} from '../../services/game.service';
import {Subscription} from 'rxjs';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {Player} from '../../model/player';
import {AuthorizationService} from '../../services/authorization.service';
import {Round} from '../../model/round';
import {RoomService} from '../../services/room.service';

@Component({
  selector: 'app-room',
  templateUrl: './game-room.component.html',
  styleUrls: ['./game-room.component.scss']
})
export class GameRoomComponent implements OnInit, OnDestroy {
  room: Room;
  player: Player;
  roomSubscription: Subscription;
  roundSubscription: Subscription;
  done: boolean;
  round: Round;
  joined: boolean;
  joinRoomInterval: any;
  getRoundInterval: any;

  constructor(private curRouter: ActivatedRoute, private router: Router, private gameService: GameService,
              private websocketService: RxStompService, private authorizationService: AuthorizationService,
              private roomService: RoomService) {
  }

   ngOnInit() {
     const roomId = this.curRouter.snapshot.paramMap.get('id') as unknown;

     this.getRoom(roomId as number);

     this.joinRoomInterval = setInterval(() => {
       if (this.room) {
         this.initializeRoomConnection();
         this.joinRoom();
         clearInterval(this.joinRoomInterval);
       }
     }, 100);

     this.getRoundInterval = setInterval(() => {
       if (this.joined) {
         this.initializeRoundConnection();
         this.getCurrentRound();
         this.done = true;
         clearInterval(this.getRoundInterval);
       }
     }, 100);
   }

  ngOnDestroy(): void {
    this.leaveRoom();
    this.roomSubscription.unsubscribe();
    this.roundSubscription.unsubscribe();
  }

  initializeRoomConnection() {
    this.roomSubscription = this.websocketService.watch('/room/join/' + this.room.id).subscribe((message: Message) => {
      if (message) {
        const player = JSON.parse(message.body) as Player;
        if (player.userId === this.authorizationService.getUserId()) {
          this.player = player;
          this.getRoom(this.room.id);
          this.joined = true;
        } else {
          this.getRoom(this.room.id);
        }
      }
    }, error => {
      console.log(error.error.error_description);
    });
  }

  initializeRoundConnection() {
    this.roundSubscription = this.websocketService.watch('/room/receive-round/' + this.room.id).subscribe((message: Message) => {
      if (message) {
        this.round = JSON.parse(message.body) as Round;
        // console.log(this.round);
      }
    }, error => {
      console.log(error.error.error_description);
    });
  }

  getRoom(id: number): void {
    this.gameService.getRoom(id).subscribe(room => {
      this.room = room as Room;

      if (this.room.playersInRoom.length >= this.room.gameRules.maxPlayerCount) {
        this.navigateToOverview();
      }
    });
  }

  getCurrentRound(): void {
    this.websocketService.publish({
      destination: '/rooms/' + this.room.id + '/get-current-round'
    });
  }

  /**
   * Returns the players that are in the room including yourself.
   */
  getAllPlayers(): Object[] {
    return this.room.playersInRoom;
  }

  /**
   * This function is called when a page is refreshed.
   *
   * @param event The refresh event.
   */
  @HostListener('window:beforeunload', ['$event']) unloadHandler(event: Event) {
    this.leaveRoom();
  }

  /**
   * Calls the leave room API call in the game service.
   */
  private leaveRoom(): void {
    this.gameService.leaveRoom(this.room.id).subscribe();
  }

  /**
   * Joins the web-instance user to this room.
   */
  private joinRoom(): void {
    this.websocketService.publish({
      destination: '/rooms/' + this.room.id + '/join',
      body: JSON.stringify({userId: this.authorizationService.getUserId(), access_token: localStorage.getItem('jwt_token')})
    });
  }

  /**
   * Navigates to the rooms overview.
   */
  private navigateToOverview(): void {
    this.router.navigateByUrl('/rooms').then(/* DO NOTHING WITH PROMISE */);
  }
}
