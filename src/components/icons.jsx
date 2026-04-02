import {
  Banknote,
  BriefcaseBusiness,
  Car,
  Clapperboard,
  CreditCard,
  DollarSign,
  GraduationCap,
  HeartPulse,
  Home,
  PiggyBank,
  ShoppingCart,
  Sparkles,
  Tv,
  UtensilsCrossed,
  Wallet,
  Zap,
} from "lucide-react";

export const categoryIconMap = {
  food: UtensilsCrossed,
  dining: UtensilsCrossed,
  grocery: ShoppingCart,
  transport: Car,
  travel: BriefcaseBusiness,
  shopping: ShoppingCart,
  salary: Banknote,
  income: DollarSign,
  freelance: BriefcaseBusiness,
  entertainment: Clapperboard,
  netflix: Tv,
  subscriptions: CreditCard,
  health: HeartPulse,
  healthcare: HeartPulse,
  utilities: Zap,
  education: GraduationCap,
  rent: Home,
  insurance: Wallet,
  investments: PiggyBank,
};

export function getCategoryIcon(category) {
  const key = (category || "").toLowerCase();
  const match = Object.keys(categoryIconMap).find((k) => key.includes(k));
  return match ? categoryIconMap[match] : Wallet;
}

export const AppIcons = {
  income: ArrowUpRightFallback,
  expense: ArrowDownRightFallback,
  insights: Sparkles,
};

function ArrowUpRightFallback(props) {
  return <DollarSign {...props} />;
}

function ArrowDownRightFallback(props) {
  return <Wallet {...props} />;
}
