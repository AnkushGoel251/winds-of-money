import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({providedIn:'root'})
export class StockService {
  backend = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getStock(symbol: string): Observable<any> {
    return this.http.get(`${this.backend}/stock?symbol=${encodeURIComponent(symbol)}`);
  }
}