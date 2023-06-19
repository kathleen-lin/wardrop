import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ItemService } from '../item.service';
import { Item } from '../model';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {

  user!: string|null

  topitems!: Item[]

  isListEmpty: boolean = false

  constructor (private itmSvc: ItemService) {}

  ngOnInit(): void {
      this.user = localStorage.getItem("user")
      console.log("From localstorage: " + this.user)
      if (this.user != null) {
        // fetch query
        this.itmSvc.getTop3(this.user)
        .then((r) => {
          // @ts-ignore
          this.topitems = r
          if (this.topitems.length == 0){
            this.isListEmpty = true 
          }
          console.log(this.topitems)
        })
        .catch((err) => console.log(err))

      }
  }


}
