import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ItemService } from '../item.service';
import { Item } from '../model';
import { MatDialog } from '@angular/material/dialog';
import { RemovalComponent } from './removal.component';

@Component({
  selector: 'app-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.css']
})
export class ItemComponent implements OnInit {

  // get from activated route
  itemId!: number
  
  selectedItem!: Item

  reason!: string

  constructor(private httpClient: HttpClient, private activatedRoute: ActivatedRoute, private itmSvc: ItemService, private router: Router, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      async (params)=> {
        this.itemId = params['itemId'];
        console.log(">>> itemId: " + this.itemId);   
        this.itmSvc.getItemById(this.itemId)
          .then((result) => {
                    this.selectedItem = result      
                    console.log("RESULT:" + result)})
          .catch((err) => console.log(err))
      }
    );
  }

  increaseWorn() {
    this.selectedItem.timeWorn++
    // update the database
    this.itmSvc.increaseTimeWorn(this.selectedItem.itemId)
      .then((r) => console.log(r))
      .catch((err) => console.log(err))
    this.ngOnInit()
  }

  remove() {
    const dialogRef = this.dialog.open(RemovalComponent, {
      data: {itemId: this.itemId, reason: this.reason}
    })
    
  }



}
