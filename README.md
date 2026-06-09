#  Space Connect — FIAP Global Solution 2026.1

> **Tema:** Space Connect — Tecnologia Espacial Aplicada a Desafios Reais  
> **Disciplina:** Android Kotlin Developer  
> **Turma:** 3º Ano Sistemas de Informação — Turmas Agosto

---

##  Descrição da Solução

**Space Connect** é um aplicativo Android nativo que conecta o usuário ao ecossistema de dados espaciais da NASA em tempo real. A proposta está alinhada ao tema da Global Solution 2026.1: conectar a exploração espacial com impacto positivo na Terra, usando dados orbitais para informar, educar e engajar pessoas com a nova economia espacial.

O app consome três APIs públicas e gratuitas da NASA:

| API | Dados |
|-----|-------|
| **APOD** (Astronomy Picture of the Day) | Imagem astronômica do dia + descrição |
| **NEO Feed** (Near Earth Objects) | Asteroides que passarão perto da Terra |
| **Mars Rover Photos** | Fotos tiradas pelo rover Curiosity em Marte |

---

##  Tema da Global Solution

**Space Connect** — Tecnologia Espacial Aplicada a Desafios Reais  
ODS relacionados: ODS 9 (Indústria, inovação e infraestrutura), ODS 13 (Ação climática)

O app demonstra como dados espaciais podem ser acessíveis ao público geral, promovendo:
- Consciência sobre objetos próximos à Terra (asteroides)
- Monitoramento espacial e ambiental via dados de satélite
- Educação sobre missões espaciais reais (Curiosity, APOD)

---

##  Fluxo de Telas

```
Splash Screen
    │
    ├─── (primeira vez) ──→ Onboarding (4 páginas) ──→ Home
    │
    └─── (já viu intro) ──────────────────────────→ Home
                                                       │
                                             ┌─────────┼─────────┐
                                             ↓         ↓         ↓
                                         NEO Radar  Galeria   Favoritos
                                         (Asteroides) Marte
```

### Telas principais (6 telas no total):

1. **Splash Screen** — Logo + animação, verifica SharedPreferences
2. **Onboarding** — 4 slides com emoji, título, descrição, botões avançar/voltar
3. **Home (APOD)** — Busca + cards de acesso rápido + lista de imagens NASA
4. **NEO Radar** — Lista de asteroides com filtro de perigosos + badge de risco
5. **Galeria de Marte** — Grid 2 colunas com fotos reais do rover, filtro por câmera
6. **Favoritos** — Lista de itens salvos (APOD / NEO / Marte) com remoção

---

##  Prints das Telas


| Splash | Onboarding | Home |
|--------|-----------|------|
| ![splash](docs/splash.png) | ![onboarding](docs/onboarding.png) | <img width="1080" height="2400" alt="Screenshot_20260609_182210" src="https://github.com/user-attachments/assets/e2caee4e-9c08-412c-8a45-a1fc133e3ca1" />
 |

| NEO Radar | Galeria Marte | Favoritos |
|-----------|--------------|-----------|
| <img width="1080" height="2400" alt="Screenshot_20260609_182349" src="https://github.com/user-attachments/assets/09258a8f-1446-48aa-8858-ac10939e56a6" />
 |<img width="1080" height="2400" alt="Screenshot_20260609_182448" src="https://github.com/user-attachments/assets/19c2bd2a-4631-402c-930e-790518a4e9a1" />
 |<img width="1080" height="2400" alt="Screenshot_20260609_182516" src="https://github.com/user-attachments/assets/07f4e3fd-43c6-4ef8-ab3d-217494727452" />
 |

---

##  APIs Utilizadas

### NASA Open APIs (gratuitas)
- **Base URL:** `https://api.nasa.gov/`
- **API Key:** `Trhk9ov5vRQjWZevh3G1SrsnmnOvWmDn0CDHAs29` 

| Endpoint | Descrição |
|----------|-----------|
| `GET /planetary/apod?count=15` | 15 imagens aleatórias do dia |
| `GET /neo/rest/v1/feed?start_date=...&end_date=...` | NEOs dos últimos 7 dias |
| `GET /mars-photos/api/v1/rovers/curiosity/photos?sol=1000` | Fotos do sol 1000 em Marte |

---

##  Explicação da Arquitetura

O projeto segue **Clean Architecture** com separação em 3 camadas:

```
app/src/main/java/br/com/fiap/spaceconnect/
│
├── data/                          ← Camada de Dados
│   ├── remote/
│   │   ├── api/NasaApiService.kt  ← Interface Retrofit
│   │   └── dto/NasaDto.kt         ← DTOs (respostas da API)
│   ├── model/FavoriteEntity.kt    ← Entidade Room + DAO + Database
│   └── repository/
│       └── RepositoryImpl.kt      ← Implementações dos repos
│
├── domain/                        ← Camada de Domínio (regras de negócio)
│   ├── model/SpaceModels.kt       ← Modelos de domínio puros
│   ├── repository/Repositories.kt ← Interfaces dos repositórios
│   └── usecase/UseCases.kt        ← 8 casos de uso específicos
│
├── presentation/                  ← Camada de Apresentação
│   ├── components/
│   │   └── SpaceComponents.kt     ← Componentes reutilizáveis Compose
│   ├── navigation/
│   │   └── SpaceConnectNavGraph.kt← Rotas + NavHost
│   ├── screens/
│   │   ├── splash/                ← Tela Splash
│   │   ├── onboarding/            ← Tela Onboarding (4 slides)
│   │   ├── home/                  ← Tela Home (APOD)
│   │   ├── missions/              ← NEO Radar + Mars Gallery
│   │   └── favorites/             ← Tela Favoritos
│   └── viewmodel/
│       └── ViewModels.kt          ← 5 ViewModels com StateFlow
│
├── di/AppModule.kt                ← Injeção de dependência (Hilt)
├── utils/PreferencesManager.kt    ← DataStore Preferences (Onboarding)
├── ui/theme/SpaceConnectTheme.kt  ← Design system (cores, tipografia)
├── MainActivity.kt
└── SpaceConnectApplication.kt     ← @HiltAndroidApp
```

### Padrões aplicados:
- **UiState selado** (`Initial / Loading / Success / Error`) em todos os ViewModels
- **StateFlow** para reatividade com `collectAsStateWithLifecycle()`
- **Hilt** para injeção de dependências em toda a cadeia
- **Room** para persistência local de favoritos
- **DataStore Preferences** (substituto moderno do SharedPreferences) para onboarding
- **Retrofit + OkHttp** para consumo das APIs NASA com logging
- **Coil** para carregamento assíncrono de imagens

---

##  Tecnologias e Dependências

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| Kotlin | 2.0.0 | Linguagem |
| Jetpack Compose BOM | 2024.08.00 | UI |
| Navigation Compose | 2.7.7 | Navegação |
| Hilt | 2.51.1 | Injeção de dependência |
| Retrofit | 2.11.0 | Consumo de API REST |
| OkHttp Logging | 4.12.0 | Debug de rede |
| Room | 2.6.1 | Banco local |
| DataStore Preferences | 1.1.1 | Preferências do usuário |
| Coil | 2.7.0 | Carregamento de imagens |
| Material 3 | (BOM) | Design system |
| Coroutines | 1.8.1 | Assincronismo |
| Splash Screen API | 1.0.1 | Tela de splash nativa |

---

##  Como Executar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 11+
- Android SDK 26+

### Passos
```bash
# 1. Clone o repositório
git clone https://github.com/ArttemiZ/SpaceConnect.git

# 2. Abra no Android Studio
# File → Open → selecione a pasta space-connect

# 3. Execute no emulador ou dispositivo físico (API 26+)
# Run → Run 'app'
```

---

##  Desenvolvedor(es)

| Nome | RM |
|------|----|
| Inacia dos Santos Silva | RM 553401 |
| Tony Khaled Osman  | RM 553050 |
---

##  Vídeo Pitch

🎬 https://youtu.be/VqXEzRIPUy0

---

##  Checklist de Requisitos

- [x] Tela de Splash com animação e verificação de SharedPreferences
- [x] Tela de Onboarding (4 slides, botões avançar/voltar, salva preferência)
- [x] Navigation Compose com 4+ telas principais
- [x] Consumo de WebService com Retrofit (NASA API)
- [x] Tratamento de estado: Initial / Loading / Success / Error
- [x] Clean Architecture (data / domain / presentation)
- [x] Componentes Compose: Column, Row, Card, LazyColumn, LazyVerticalGrid, Scaffold, TopAppBar
- [x] Interações: favoritar, filtrar, buscar, remover favorito
- [x] ViewModel com StateFlow e separação de responsabilidades
- [x] Componentes reutilizáveis (ApodCard, NeoCard, MarsPhotoCard, SpaceFilterChip...)
- [x] Persistência local com Room (favoritos)
- [x] DataStore Preferences (onboarding)
- [x] Hilt para DI
- [x] Design system coerente com tema espacial
